package org.vlis.operations.event.typeguess.storageutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlis.operations.event.typeguess.config.OperationConfig;

import com.alibaba.fastjson.JSON;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;

public class ElasticSearchHuntUtil {

	private ElasticSearchHuntUtil() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchHuntUtil.class);

	private static final Client client = ElasticSearchClient.getInstance().getElasticSearchClient();

	private static final String PARENTSPAN_ID = "parentSpanId";
	private static final String TRANSACTION_ID = "transactionId";
	private static final String URL = "url";

	public static void loopDocs(SearchHit[] docs) {
		for (SearchHit doc : docs) {
			Map<String, Object> data = doc.getSource();
			System.out.println("thinking_fioa111");
			System.out.println("data: " + data.toString());
			System.out.println("thinking_fioa222");
		}
	}

	public static boolean isExist(String... indexs) {
		try {
			IndicesExistsRequest indicesExistsResponse = new IndicesExistsRequest(indexs);
			IndicesExistsResponse getResponse = client.admin().indices().exists(indicesExistsResponse).get();
			return getResponse.isExists();
		} catch (ExecutionException exe) {
			LOGGER.error("happen ExecutionException");
			exe.printStackTrace();
		} catch (InterruptedException interExe) {
			LOGGER.error("happen InterruptedException");
			interExe.printStackTrace();
		}
		return false;

	}

	public static List<String> getAllOpeationMasterIps() {
		if (!isExist(OperationConfig.getEsIndexPrefix() + "*")) {
			return null;
		}
		List<String> masterIps = new ArrayList<String>();
		SearchResponse serarchResponse = client.prepareSearch(OperationConfig.getEsIndexPrefix() + "*").setSize(0)
				.addAggregation(AggregationBuilders.terms("masterIps").field("_index").size(0)).execute().actionGet();
		if (serarchResponse.getHits().getTotalHits() > 0) {
			Terms masterIpsTerms = serarchResponse.getAggregations().get("masterIps");
			List<Bucket> masterIpsBucket = masterIpsTerms.getBuckets();
			for (Bucket masterIp : masterIpsBucket) {
				masterIps.add(masterIp.getKey().toString().substring(OperationConfig.getEsIndexPrefix().length()));
			}
		}
		return masterIps;
	}

	public static Map<String, String> getTransactionIdUrlPair(final String masterIp) {
		if (null == masterIp || masterIp.isEmpty()) {
			throw new NullPointerException();
		}
		if (!isExist(OperationConfig.getEsIndexPrefix() + masterIp)) {
			return null;
		}
		// Map<transactionId, Url>
		Map<String, String> tidAndUrlPair = new HashMap<String, String>();
		// FilterBuilder filter =
		// FilterBuilders.andFilter(FilterBuilders.termFilter(PARENTSPAN_ID,
		// -1));
		QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termQuery(PARENTSPAN_ID, -1));
		TermsBuilder agg = AggregationBuilders.terms("transactionIds").field(TRANSACTION_ID).size(0)
				.subAggregation(AggregationBuilders.terms("urls").field(URL).size(0));
		SearchResponse sr = client.prepareSearch(OperationConfig.getEsIndexPrefix() + masterIp)
				.setTypes(OperationConfig.getEsType()).setQuery(queryBuilder).addAggregation(agg).setSize(0).execute()
				.actionGet();
		Terms transactionIdBuckets = sr.getAggregations().get("transactionIds");
		for (Bucket transactionIdBucket : transactionIdBuckets.getBuckets()) {
			String transactionId = transactionIdBucket.getKey().toString().trim();
			Terms urlBuckets = transactionIdBucket.getAggregations().get("urls");
			if (urlBuckets.getBuckets().size() != 1) {
				throw new IllegalStateException();
			}
			for (Bucket urlBucket : urlBuckets.getBuckets()) {
				String url = urlBucket.getKey().toString().trim();
				tidAndUrlPair.put(transactionId, url);
			}
		}
		return tidAndUrlPair;
	}

	public static SearchHit[] getDocs(final String masterIp) {
		if (null == masterIp || masterIp.isEmpty()) {
			throw new NullPointerException();
		}

		if (!isExist(OperationConfig.getEsIndexPrefix() + masterIp)) {
			throw new NullPointerException();
		}

		return getDocs0(OperationConfig.getEsIndexPrefix(), masterIp);
	}

	public static SearchHit[] getUnDealWithDocs(final String masterIp) {
		if (null == masterIp || masterIp.isEmpty()) {
			throw new NullPointerException();
		}

		if (!isExist(OperationConfig.getEsUnDealWithIndexPrefix() + masterIp)) {
			return null;
		}

		return getDocs0(OperationConfig.getEsUnDealWithIndexPrefix(), masterIp);
	}

	private static SearchHit[] getDocs0(final String indexPrefix, final String masterIp) {

		SearchResponse srHitCounts = client.prepareSearch(indexPrefix + masterIp).setTypes(OperationConfig.getEsType())
				.setSize(0).execute().actionGet();

		int hitCount = (int) srHitCounts.getHits().getTotalHits();
		SearchResponse searchResponse = client.prepareSearch(indexPrefix + masterIp).setSize(hitCount).execute()
				.actionGet();
		return searchResponse.getHits().getHits();
	}

	public static Map<String, String> getBuckupTransactionIdUrlPair(final String masterIp) {
		if (null == masterIp || masterIp.isEmpty()) {
			throw new NullPointerException();
		}
		if (!isExist(OperationConfig.getEsBackupIndexPrefix() + masterIp)) {
			return null;
		}
		// Map<transactionId, url>
		Map<String, String> tidAndUrlPair = new HashMap<String, String>();
		SearchResponse srHitCounts = client.prepareSearch(OperationConfig.getEsBackupIndexPrefix() + masterIp)
				.setTypes(OperationConfig.getEsType()).setSize(0).execute().actionGet();

		int hitCount = (int) srHitCounts.getHits().getTotalHits();
		SearchResponse searchResponse = client.prepareSearch(OperationConfig.getEsBackupIndexPrefix() + masterIp)
				.setTypes(OperationConfig.getEsType()).setSize(hitCount).execute().actionGet();
		for (SearchHit hit : searchResponse.getHits().getHits()) {
			@SuppressWarnings("unchecked")
			List<Map<String, String>> backupKeyTypeList = (List<Map<String, String>>) hit.getSource()
					.get("backupKeyType");
			for (Map<String, String> map : backupKeyTypeList) {
				tidAndUrlPair.put(map.get(TRANSACTION_ID), map.get(URL));
			}
		}
		return tidAndUrlPair;
	}

	// public static List<String> searcher( String indexname, String type,String
	// jsonPath,String basedUrl){
	// List<String> values=new ArrayList<String>();
	// String[] path=jsonPath.split(".");
	// int last=path.length;
	// String cloumnName=path[last];
	//
	// QueryBuilder queryBuilder = QueryBuilders.
	// SearchResponse searchResponse =
	// client.prepareSearch(indexname).setTypes(type)
	// .setQuery(queryBuilder)
	// .execute()
	// .actionGet();
	// SearchHits hits = searchResponse.getHits();
	// System.out.println("查询到记录数=" + hits.getTotalHits());
	// SearchHit[] searchHists = hits.getHits();
	// if(searchHists.length>0){
	// for(SearchHit hit:searchHists){
	// String value = (String) hit.getSource().get(cloumnName);
	// values.add(value);
	// }
	// }
	// return values;
	// }
	/**
	 * 从ES中取jsonPath只向的数据
	 * 
	 * @param basedUrl
	 * @param jsonPath
	 * @return null，如果获取数据为空；否则，返回所有记录该字段值的集合
	 */
	public static List<Object> getAllRecordsByRootUrl(String rootUrl) {
		QueryBuilder qb2 = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("url", rootUrl));
		List<Object> pureJsonSet = new ArrayList<Object>();
		SearchResponse responsesearch = client.prepareSearch(OperationConfig.getEsIndexPrefix() + "*")
				.setTypes(OperationConfig.getEsType()).setQuery(qb2).addSort("updateTime", SortOrder.ASC)
				.addField("nodes").execute().actionGet();
		SearchHits searchHits = responsesearch.getHits();
		String nativeESJson = null;
		JSONArray hitsDatas = null;
		 for(SearchHit hit:searchHits){
              
			 String nodes=hit.getFields().get("nodes").getValue();
			 System.out.println(nodes);
			 pureJsonSet.add(JSON.parse(nodes));
		 }		 
//		if (searchHits.getTotalHits() > 0) {
//			nativeESJson = responsesearch.toString();
//			// System.out.println(nativeESJson);
//			hitsDatas = JsonPath.read(nativeESJson, "$.hits.hits");
//		} else
//			return null;
//		for (Object data : hitsDatas) {
//			Object signleObj = JsonPath.read(data, "$.fields.nodes.[0]");
//			System.out.println(signleObj);
//			
//		}
		return pureJsonSet;
	}
}
