package org.vlis.operations.event.typeguess.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class OperationConfig {
	private static final Logger LOGGER = LogManager.getLogger(OperationConfig.class);

	public static void init() throws Exception {
		InitOperationEnvironmentConfig.init();
	}

	public static String getEsIndexPrefix() {
		return InitOperationEnvironmentConfig.getEsIndexPrefix();
	}

	public static String getEsBackupIndexPrefix() {
		return InitOperationEnvironmentConfig.getBackupEsIndexPrefix();
	}

	public static String getEsUnDealWithIndexPrefix() {
		return InitOperationEnvironmentConfig.getUnDealWithIndexPrefix();
	}

	public static String getEsType() {
		return InitOperationEnvironmentConfig.getEsType();
	}

	public static String getEsClusterName() {
		return InitOperationEnvironmentConfig.getEsClusterName();
	}

	public static Map<String, Integer> getESHosts() {
		return InitOperationEnvironmentConfig.getEsHosts();
	}

	public static int getEsBatchProcessingInteval() {
		return InitOperationEnvironmentConfig.getEsEntryUrlDealInterval();
	}

	public static String getDriverDB() {
		return InitOperationEnvironmentConfig.getC3p0_driverClass();
	}

	public static String getUserNameDB() {
		return InitOperationEnvironmentConfig.getC3p0_user();
	}

	public static String getPasswdDB() {
		return InitOperationEnvironmentConfig.getC3p0_password();
	}

	public static String getConnectionUrlDB() {
		return InitOperationEnvironmentConfig.getC3p0_url();
	}

}
