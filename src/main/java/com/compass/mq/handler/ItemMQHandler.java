package com.compass.mq.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.annotation.Autowired;

import com.compass.pojo.Item;
import com.compass.service.ItemService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ItemMQHandler {
	
	@Autowired
	private HttpSolrServer httpSolrServer;
	@Autowired
	private ItemService itemService;
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	public void execute(String msg) {
		try {
			JsonNode jsonNode = MAPPER.readTree(msg);
			Long itemId = jsonNode.get("itemId").asLong();
			String type = jsonNode.get("type").asText();
			if (StringUtils.equals(type, "insert") || StringUtils.equals(type, "update")) {
				Item item = this.itemService.queryItemById(itemId);
				if(null != item) {
					this.httpSolrServer.addBean(item);
					this.httpSolrServer.commit();
				}
			} else if (StringUtils.equals(type, "delete")) {
				this.httpSolrServer.deleteById(String.valueOf(itemId));
				this.httpSolrServer.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
