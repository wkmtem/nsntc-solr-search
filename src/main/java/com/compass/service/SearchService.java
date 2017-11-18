package com.compass.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.compass.bean.SearchResult;
import com.compass.constant.Constant;
import com.compass.pojo.Item;

@Service
public class SearchService {
	
	@Autowired
	private HttpSolrServer httpSolrServer;

	public SearchResult search(String keyWords, Integer pageNum) throws Exception {
		/**
         * 构造搜索条件
         */
        SolrQuery solrQuery = new SolrQuery();
        /**
         * 搜索关键词
         */
        solrQuery.setQuery("title:" + keyWords + " AND status:1");
        /**
         * 设置分页:start=0从0开始，rows=5当前返回5条记录
         */
        solrQuery.setStart((Math.max(pageNum, 1) - 1) * Constant.ROWS);
        solrQuery.setRows(Constant.ROWS);

        /**
         * 是否需要高亮
         */
        boolean isHighlighting = !StringUtils.equals("*", keyWords) && StringUtils.isNotEmpty(keyWords);

        /**
         * 高亮
         */
        if (isHighlighting) {
            /**
             * 开启高亮组件
             */
            solrQuery.setHighlight(true);
            /**
             * 高亮字段
             */
            solrQuery.addHighlightField("title");
            /**
             * 标记，高亮关键字前缀
             */
            solrQuery.setHighlightSimplePre("<em>");
            /**
             * 后缀
             */
            solrQuery.setHighlightSimplePost("</em>");
        }

        /**
         * 执行查询
         */
        QueryResponse queryResponse = this.httpSolrServer.query(solrQuery);
        List<Item> itemList = queryResponse.getBeans(Item.class);
        if (isHighlighting) {
            /**
             * 将高亮的标题数据写回到数据对象中
             */
            Map<String, Map<String, List<String>>> map = queryResponse.getHighlighting();
            for (Map.Entry<String, Map<String, List<String>>> highlighting : map.entrySet()) {
                for (Item item : itemList) {
                    if (!highlighting.getKey().equals(item.getId().toString())) {
                        continue;
                    }
                    item.setTitle(StringUtils.join(highlighting.getValue().get("title"), ""));
                    break;
                }
            }
        }
        return new SearchResult(queryResponse.getResults().getNumFound(), itemList);
	}
}
