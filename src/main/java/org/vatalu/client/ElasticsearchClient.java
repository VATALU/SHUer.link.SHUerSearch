package org.vatalu.client;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

@Repository
public class ElasticsearchClient {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public List<Map<String,String>> getFullTextQuery(String keyword,int start,int size) {
        TransportClient client = null;
        try {
            Settings settings = Settings.builder()
                    .put("cluster.name", "elasticsearch_production")
                    .build();
            client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("118.25.21.224"),9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        QueryBuilder queryBuilder = multiMatchQuery(keyword,"result.content","result.title");
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                                            .field(new HighlightBuilder.Field("result.title").fragmentSize(50))
                                            .field(new HighlightBuilder.Field("result.content").fragmentSize(300).numOfFragments(3));
            SearchResponse response = client.prepareSearch("shu")
                    .setTypes("page")
                    .setQuery(queryBuilder)
                    .highlighter(highlightBuilder)
                    .setFrom(start).setSize(size).setExplain(false)
                    .get();
            client.close();
            if(!response.isTimedOut()) {
                SearchHits searchHits = response.getHits();
                List<Map<String,String>> searchResultMapList = new ArrayList<>();
                for(SearchHit searchHit:searchHits){
                    searchResultMapList.addAll(new ArrayList(searchHit.getSourceAsMap().values()));
                    Text[] texts = searchHit.getHighlightFields().get("result.content").getFragments();
                    StringBuffer stringBuffer = new StringBuffer();
                    for(Text text :texts){
                        stringBuffer.append(text.toString());
                        stringBuffer.append("......");
                    }
                    Map<String,String> searchResultMap = searchResultMapList.get(0);
                    searchResultMap.put("content",stringBuffer.toString());
                }
                return searchResultMapList;
            }
        return null;
    }

}
