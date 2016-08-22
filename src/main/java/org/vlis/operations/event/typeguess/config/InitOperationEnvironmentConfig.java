package org.vlis.operations.event.typeguess.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * @author thinking_fioa , E-mail: thinking_fioa@163.com Create Time :
 *         2016年6月13日下午1:50:27
 *
 */
public class InitOperationEnvironmentConfig {

	private static final Logger LOGGER = LogManager.getLogger(InitOperationEnvironmentConfig.class);
	private static final String CONFIGNAME = "config.properties";
	// 默认参数
	// DB
	private static String c3p0_url = "jdbc:mysql://10.10.102.101:3306/vlis_analytics?useUnicode=true&amp;characterEncoding=UTF-8";
	private static String c3p0_user = "buynow";
	private static String c3p0_password = "buynow";
	private static String c3p0_driverClass = "com.mysql.jdbc.Driver";
	private static int c3p0_acquireIncrement = 1;
	private static int c3p0_maxIdleTime = 60;
	private static int c3p0_maxPoolSize = 10;
	private static int c3p0_minPoolSize = 2;
	private static int c3p0_initialPoolSize = 3;
	// ES Config
	private static String esIndexPrefix = "operations_first_";
	private static String backupEsIndexPrefix = "operations_backup_keytypepair_";
	private static String unDealWithIndexPrefix = "operations_second_";
	private static String esType = "recordEvent";
	private static String esClusterName = "Apm";
	private static String esHostAddress = "[10.10.103.102:9200,10.10.103.112:9200,10.10.102.101:9200]";
	private static Map<String, Integer> esHosts = null;
	private static int esEntryUrlDealInterval = 120000;

	public static void init() throws Exception {
		LOGGER.info(" Reading Environment Configuration:" + CONFIGNAME);
		InputStream in = InitOperationEnvironmentConfig.class.getClassLoader().getResourceAsStream(CONFIGNAME);
		Properties props = new Properties();
		try {
			props.load(in);
		} catch (IOException e) {
			LOGGER.error("Init Config failed.", e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Map<String, Object> map = ConfigFileHelper.getConfigSettings(props);

		// DB
		c3p0_url = BaseConfig.getStringProperty(map, "c3p0_url", c3p0_url);
		c3p0_user = BaseConfig.getStringProperty(map, "c3p0_user", c3p0_user);
		c3p0_password = BaseConfig.getStringProperty(map, "c3p0_password", c3p0_password);
		c3p0_driverClass = BaseConfig.getStringProperty(map, "c3p0_driverClass", c3p0_driverClass);
		c3p0_acquireIncrement = BaseConfig.getIntProperty(map, "c3p0_acquireIncrement", c3p0_acquireIncrement);
		c3p0_maxIdleTime = BaseConfig.getIntProperty(map, "c3p0_maxIdleTime", c3p0_maxIdleTime);
		c3p0_maxPoolSize = BaseConfig.getIntProperty(map, "c3p0_maxPoolSize", c3p0_maxPoolSize);
		c3p0_minPoolSize = BaseConfig.getIntProperty(map, "c3p0_minPoolSize", c3p0_minPoolSize);
		c3p0_initialPoolSize = BaseConfig.getIntProperty(map, "c3p0_initialPoolSize", c3p0_initialPoolSize);
		// ES
		esIndexPrefix = BaseConfig.getStringProperty(map, "es_index_prefix", esIndexPrefix);
		backupEsIndexPrefix = BaseConfig.getStringProperty(map, "backup_es_index_prefix", backupEsIndexPrefix);
		unDealWithIndexPrefix = BaseConfig.getStringProperty(map, "undealwith_es_index_prefix", unDealWithIndexPrefix);
		esType = BaseConfig.getStringProperty(map, "es_type", esType);
		esClusterName = BaseConfig.getStringProperty(map, "es_cluster_name", esClusterName);
		esHostAddress = BaseConfig.getStringProperty(map, "es_host_address", esHostAddress);
		esHosts = analyzeESHostsAddress(esHostAddress);
		esEntryUrlDealInterval = BaseConfig.getIntProperty(map, "es_entry_url_dealwith_interval ",
				esEntryUrlDealInterval);
	}

	/*
	 * 解析配置文件中ES各个节点的ip地址和端口
	 */
	private static Map<String, Integer> analyzeESHostsAddress(String esHostsAddress) {
		if (null == esHostsAddress || esHostsAddress.isEmpty()) {
			throw new NullPointerException();
		}

		Map<String, Integer> hosts = new HashMap<String, Integer>();
		esHostsAddress = esHostsAddress.substring(1, esHostsAddress.length() - 1).replaceAll(" ", "").trim();
		String[] ipPorts = esHostsAddress.split(",");
		for (String ipPort : ipPorts) {
			String[] ipAndPort = ipPort.split(":");
			hosts.put(ipAndPort[0], Integer.valueOf(ipAndPort[1]));
		}
		if (hosts.isEmpty()) {
			throw new NullPointerException();
		}
		return hosts;
	}

	public static String getC3p0_url() {
		return c3p0_url;
	}

	public static String getC3p0_user() {
		return c3p0_user;
	}

	public static String getC3p0_password() {
		return c3p0_password;
	}

	public static String getC3p0_driverClass() {
		return c3p0_driverClass;
	}

	public static int getC3p0_acquireIncrement() {
		return c3p0_acquireIncrement;
	}

	public static int getC3p0_maxIdleTime() {
		return c3p0_maxIdleTime;
	}

	public static int getC3p0_maxPoolSize() {
		return c3p0_maxPoolSize;
	}

	public static int getC3p0_minPoolSize() {
		return c3p0_minPoolSize;
	}

	public static int getC3p0_initialPoolSize() {
		return c3p0_initialPoolSize;
	}

	public static Logger getLogger() {
		return LOGGER;
	}

	public static String getConfigname() {
		return CONFIGNAME;
	}

	public static String getEsIndexPrefix() {
		return esIndexPrefix;
	}

	public static String getBackupEsIndexPrefix() {
		return backupEsIndexPrefix;
	}

	public static String getUnDealWithIndexPrefix() {
		return unDealWithIndexPrefix;
	}

	public static String getEsType() {
		return esType;
	}

	public static String getEsClusterName() {
		return esClusterName;
	}

	public static String getEsHostAddress() {
		return esHostAddress;
	}

	public static Map<String, Integer> getEsHosts() {
		return esHosts;
	}

	public static int getEsEntryUrlDealInterval() {
		return esEntryUrlDealInterval;
	}
}
