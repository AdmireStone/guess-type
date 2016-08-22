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


	/**
	 * select all nodes data include in big-json according to the url that
	 * included in the record
	 * 
	 * @param url
	 *            the url that included in the record
	 * @return a list that contains all the selected json object. each object
	 *         only contains the information of the filed "nodes" for each josn
	 *         record in the ElasticSearch
	 */
	public static List<Object> getAllRecordsByRootUrl(String url) {
		QueryBuilder qb2 = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("url", url));
		List<Object> pureJsonSet = new ArrayList<Object>();
		SearchResponse responsesearch = client.prepareSearch(OperationConfig.getEsIndexPrefix() + "*")
				.setTypes(OperationConfig.getEsType()).setQuery(qb2).addSort("updateTime", SortOrder.ASC)
				.addField("nodes").execute().actionGet();
		SearchHits searchHits = responsesearch.getHits();
		for (SearchHit hit : searchHits) {
			String nodes = hit.getFields().get("nodes").getValue();
			LOGGER.info(nodes);
			pureJsonSet.add(JSON.parse(nodes));
		}
		return pureJsonSet;
	}
}
