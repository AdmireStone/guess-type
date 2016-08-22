package org.vlis.operations.event.typeguess.storageutils;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.vlis.operations.event.typeguess.config.InitOperationEnvironmentConfig;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 没有研究数据库的关闭
 * 
 * @author dgl
 *
 */
public class MysqlOperationUtil {

	private static ComboPooledDataSource cpds = new ComboPooledDataSource();;
	private static final Logger LOGGER = LogManager.getLogger(MysqlOperationUtil.class);

	//初始化c3p0设置
	static {
		try {
			cpds.setDriverClass(InitOperationEnvironmentConfig.getC3p0_driverClass());
			cpds.setUser(InitOperationEnvironmentConfig.getC3p0_user());
			cpds.setJdbcUrl(InitOperationEnvironmentConfig.getC3p0_url());
			cpds.setPassword(InitOperationEnvironmentConfig.getC3p0_password());
			cpds.setMaxIdleTime(InitOperationEnvironmentConfig.getC3p0_maxIdleTime());
			cpds.setMaxPoolSize(InitOperationEnvironmentConfig.getC3p0_maxPoolSize());
			cpds.setAcquireIncrement(InitOperationEnvironmentConfig.getC3p0_acquireIncrement());
			cpds.setMinPoolSize(InitOperationEnvironmentConfig.getC3p0_minPoolSize());
			cpds.setInitialPoolSize(InitOperationEnvironmentConfig.getC3p0_initialPoolSize());
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getStackTrace());
		}
	}

	/**
	 * 取得链接,在使用完成后一定要手动关闭连接
	 * 
	 * @return
	 */
	public static Connection getConnection() {
		Connection connection = null;
		try {
			connection = cpds.getConnection();
		} catch (SQLException e) {
			LOGGER.error(e.getStackTrace());
		}
		return connection;
	}

	public static void closeConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.error("closeConnection failed.", e);
		}
	}

	public static boolean doInsert(PreparedStatement preparedStatement) {
		boolean restult = true;
		try {
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.error("doInsert failed.", e);
			restult = false;
		}
		return restult;
	};

	public static boolean doDelete(PreparedStatement preparedStatement) {
		boolean restult = true;
		try {
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.error("doDelete failed.", e);
			restult = false;
		}
		return restult;
	};

	public static ResultSet doSelect(PreparedStatement preparedStatement) {
		ResultSet resultSet = null;
		try {
			resultSet = preparedStatement.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.error("doSelect failed.", e);
		}
		return resultSet;
	}

	public static boolean doUpdate(PreparedStatement preparedStatement) {
		boolean restult = true;
		try {
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.error("doUpdate failed", e);
			restult = false;
		}
		return restult;
	}

	//测试所用
	public static void main(String[] args) throws Exception {

		InitOperationEnvironmentConfig.init();
		Connection conn = null;
		if ((conn = MysqlOperationUtil.getConnection()) != null) {
			System.out.println("获取链接成功！");
		}
		String sql = "select * from app_user_group";
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		ResultSet rs = MysqlOperationUtil.doSelect(preparedStatement);
		ResultSetMetaData m = null;// 获取 列信息
		m = rs.getMetaData();

		int columns = m.getColumnCount();
		// 显示列,表格的表头
		for (int i = 1; i <= columns; i++) {
			System.out.print(m.getColumnName(i));
			System.out.print("\t\t");
		}

		System.out.println();
		// 显示表格内容
		while (rs.next()) {
			for (int i = 1; i <= columns; i++) {
				System.out.print(rs.getString(i));
				System.out.print("\t\t");
			}
			System.out.println();
		}

	}
}
