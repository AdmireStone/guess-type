package org.vlis.operations.event.typeguess;

import java.security.KeyStore.Entry.Attribute;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.vlis.operations.event.typeguess.bean.EventBean;
import org.vlis.operations.event.typeguess.bean.EventBean.Key;
import org.vlis.operations.event.typeguess.config.OperationConfig;
import org.vlis.operations.event.typeguess.constant.TypeGuessConstant;
import org.vlis.operations.event.typeguess.storageutils.ElasticSearchHuntUtil;
import org.vlis.operations.event.typeguess.storageutils.MysqlOperationUtil;
import com.jayway.jsonpath.JsonPath;

/**
 * 该类用于从ElasticSearch获取URL纵向归并后的数据
 * 
 * @author dgl
 *
 */
public class AttributeTypeSpeculator implements TypeGuessVisitor {
	private Map<Integer, String> keyTypePairs = new ConcurrentHashMap<Integer, String>();
	private static final Logger LOGGER = LogManager.getLogger(AttributeTypeSpeculator.class);
	private String alterSQL = "update  vlis_analytics2.behavior_event_attribute set ATTR_TYPE=?, IS_GUESSED=?, MODIFY_TIME=?  where ATTR_ID=?";
	private String judgeGuessedSQL = "select ATTR_TYPE from  vlis_analytics2.behavior_event_attribute where  ATTR_ID=?";
	private static final ExecutorService EVENT_SERVICE = Executors.newFixedThreadPool(5);

	public void guess(EventBean eventBean) {
		// TODO Auto-generated method stub
		// 从ES取出rootUrl下面的所有jsonSet
		List<Object> jsonObjectList = ElasticSearchHuntUtil.getAllRecordsByRootUrl(eventBean.getEnterUrl());
		// 类型猜测
		for (Key key : eventBean.getKeys()) {
			if (!isGuessed(key)) {
				speculateAttributType(key, jsonObjectList, keyTypePairs);
			}
		}
		// 将类型更新到数据库
		EVENT_SERVICE.submit(new updateAttributeTypeThread(keyTypePairs));
	}

	/**
	 * 对单个属性进行类型猜测
	 * 
	 * @param attributeBean
	 *            待猜测的属性类型
	 */
	public void speculateAttributType(Key attributeBean, List<Object> jsonObjectList,
			Map<Integer, String> keyTypePairs) {
		if (jsonObjectList == null) {
			return;
		}
		List<String> values = getSpecificCloumnValues(jsonObjectList, attributeBean.getJsonPath());
		SpeculateKeyTypeCounter speculateKeyTypeCounter = new SpeculateKeyTypeCounter();
		for (String value : values) {
			speculateKeyTypeCounter.distinguishKeyAndCount(value);
		}
		keyTypePairs.put(Integer.valueOf(attributeBean.getAttributeId()), speculateKeyTypeCounter.getKeyType());
		attributeBean.setAttributeType(speculateKeyTypeCounter.getKeyType());
	}

	/**
	 * 根据jsonPath从数据集中获取指定列的值
	 * 
	 * @param jsonSet
	 *            根据rootUrl获取的数据集
	 * @param jsonPath
	 *            属性的jsonPath
	 * @return
	 */
	public List<String> getSpecificCloumnValues(List<Object> jsonObjectList, String jsonPath) {
		List<String> values = new ArrayList<String>();
		for (Object data : jsonObjectList) {
			String value = JsonPath.read(data, jsonPath);
			if (value != null) {
				values.add(value);
				LOGGER.info(value);
			}
		}
		return values;
	}

	/**
	 * 将字符串表达的数据类型转化为数据库中存储的数字类型
	 * 
	 * @param type
	 *            字符串表达的数据类型
	 * @return 对应的数字类型
	 */
	public static int convertKeyTypetoInt(String type) {
		if (type.equals(TypeGuessConstant.BOOLEAN_TYPE))
			return 1;
		if (type.equals(TypeGuessConstant.LONG_TYPE))
			return 2;
		if (type.equals(TypeGuessConstant.DOUBLE_TYPE))
			return 3;
		if (type.equals(TypeGuessConstant.STRING_TYPE))
			return 4;
		if (type.equals(TypeGuessConstant.DATE_TYPE))
			return 5;
		LOGGER.error("wrong type value", new NumberFormatException());
		return 0;
	}
	
	/**
	 * 将数据库中用数字表示的类型转换为对应的字面类型
	 * @param type
	 * @return
	 */
	public String convertKeyTypetoString(int type) {
		if (type==1)
			return TypeGuessConstant.BOOLEAN_TYPE ;
		if (type==2)
			return TypeGuessConstant.LONG_TYPE;
		if (type==3)
			return TypeGuessConstant.DOUBLE_TYPE;
		if (type==4)
			return TypeGuessConstant.STRING_TYPE;
		if (type==5)
			return TypeGuessConstant.DATE_TYPE;
		LOGGER.error("wrong type value", new NumberFormatException());
		return "unKonwType:type="+type;
	}

	public boolean isGuessed(Key key) {
		Connection conn = MysqlOperationUtil.getConnection();
		PreparedStatement prepareStatement = null;
		int ATTR_TYPE = -1;
		try {
			// 查找未被更新的记录
			prepareStatement = conn.prepareStatement(judgeGuessedSQL);
			prepareStatement.setInt(1, key.getAttributeId());
			ResultSet resultSet = MysqlOperationUtil.doSelect(prepareStatement);
			resultSet.next();
			ATTR_TYPE = resultSet.getInt("ATTR_TYPE");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.error("judge attribute type failed!", e);
		}
		if (ATTR_TYPE == 0) {
			return false;
		}
		key.setAttributeType(convertKeyTypetoString(ATTR_TYPE));
		return true;
	}

	/**
	 * 将属性类型更新到已被猜出的记录中,并将记录的猜测状态设置为已猜测
	 * 
	 * @author dgl
	 *
	 */
	class updateAttributeTypeThread implements Runnable {
		private Map<Integer, String> keyTypePairs = new ConcurrentHashMap<Integer, String>();

		public void run() {
			updateDBAttributeTypeandStatus();
		}

		public updateAttributeTypeThread(Map<Integer, String> keyTypePairs) {
			this.keyTypePairs = keyTypePairs;
		}

		public void updateDBAttributeTypeandStatus() {

			Connection conn = MysqlOperationUtil.getConnection();
			for (int attr_id : keyTypePairs.keySet()) {
				try {
					PreparedStatement prepareStatement = conn.prepareStatement(alterSQL);
					String type = keyTypePairs.get(attr_id);
					prepareStatement.setInt(1, convertKeyTypetoInt(type));
					// 状态设置为已猜测
					prepareStatement.setInt(2, 1);
					prepareStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
					prepareStatement.setInt(4, attr_id);
					MysqlOperationUtil.doUpdate(prepareStatement);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					LOGGER.error("set DB attribute type failed for prepared statement has exception", e);
				}
			}
			MysqlOperationUtil.closeConnection(conn);
		}
	}

	public static void main(String[] args) throws Exception {
		OperationConfig.init();
		List<Key> listkeys = new ArrayList<Key>();
		listkeys.add(new Key(11, "account", "$.node_0.db.t_account_select.result.[0].t_account#account", ""));
		listkeys.add(new Key(12, "grade", "$.node_0.db.t_account_select.result.[0].t_account#grade", ""));
		listkeys.add(new Key(13, "'lastLoginTime",
				"$.node_0.db.t_account_select.result.[0].t_account#lastLoginTime", ""));
		listkeys.add(
				new Key(14, "phone", "$.node_0.http.response.ns2_selectByIdResponse.[0].return.[0].phone", ""));
		EventBean evnetBean = new EventBean("inchina", 1, "", "application@10.10.105.241:8080^1469000675303^4",
				"/jeeshopserver_address/addressServiceFront", listkeys);
		AttributeTypeSpeculator task = new AttributeTypeSpeculator();
		task.guess(evnetBean);
		for(Key key:evnetBean.getKeys()){
			     System.out.println(key.getAttributeName()+":"+key.getAttributeType());
		}
	}
}
