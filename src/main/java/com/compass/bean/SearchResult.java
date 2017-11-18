package com.compass.bean;

import java.util.List;

public class SearchResult {

	private Long total;
	private List<?> dataList;
	
	public SearchResult() {
	}
	
	public SearchResult(Long total, List<?> dataList) {
		super();
		this.total = total;
		this.dataList = dataList;
	}
	
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	public List<?> getDataList() {
		return dataList;
	}
	public void setDataList(List<?> dataList) {
		this.dataList = dataList;
	}
}
