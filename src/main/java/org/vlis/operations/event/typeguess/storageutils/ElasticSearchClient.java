package org.vlis.operations.event.typeguess.storageutils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.server.Operation;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.vlis.operations.event.typeguess.config.ConfigFileHelper;
import org.vlis.operations.event.typeguess.config.InitOperationEnvironmentConfig;
import org.vlis.operations.event.typeguess.config.OperationConfig;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

final class ElasticSearchClient {

	private static final Logger LOGGER = LogManager.getLogger(ElasticSearchClient.class);
	private volatile Client client = null;
	private static final ElasticSearchClient instance = new ElasticSearchClient();

	private ElasticSearchClient() {
	}

	public static ElasticSearchClient getInstance() {
		if (null == instance) {
			throw new NullPointerException();
		}
		return instance;
	}

	public Client getElasticSearchClient() {

		Client copyClient = client;
		if (null == copyClient) {
			synchronized (this) {
				copyClient = client;
				if (copyClient == null) {
					try {
						TransportClient transportClient = TransportClient.builder().settings(Settings.settingsBuilder()
								.put("cluster.name", InitOperationEnvironmentConfig.getEsClusterName())).build();
						Map<String, Integer> esHostsSet = InitOperationEnvironmentConfig.getEsHosts();
						for (String ip : esHostsSet.keySet())
							transportClient.addTransportAddress(
									new InetSocketTransportAddress(InetAddress.getByName(ip), esHostsSet.get(ip)));
						copyClient = client = transportClient;
					} catch (UnknownHostException exception) {
						LOGGER.error("UnknowHostException,  can't be transformed to host.");
					}
				}
			}
		}
		return copyClient;
	}

	public static void main(String[] args) throws Exception {
		OperationConfig.init();
		String jsonnodes = null;
		Client esClient = ElasticSearchClient.getInstance().getElasticSearchClient();
		InputStream ins = ElasticSearchClient.class.getClassLoader().getResourceAsStream("213.json");
		if (esClient == null) {
			System.out.println("client is null");
		}
		if (ins == null) {
			System.out.println("ins is null");
		} else {
			jsonnodes = ConfigFileHelper.inputStream2String(ins);
			System.out.println(jsonnodes);
		}

		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("timestamp", "2016-07-20 15:45:23");
		map.put("transactionId", "application@10.10.105.241:8080^1469000675303^47");
		map.put("agentId", "application@10.10.105.241:8080");
		map.put("url", "/jeeshopserver_address/addressServiceFront");    
		map.put("nodes", jsonnodes);
		String json=JSON.toJSONString(map, true); 
		IndexResponse response = esClient.prepareIndex("apm-event-10.100.100.10", "Event").setSource(json).get();
	}

}
