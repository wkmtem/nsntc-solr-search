package com.compass.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.compass.bean.SearchResult;
import com.compass.constant.Constant;
import com.compass.service.SearchService;

/**
 * 
 * <p>Class Name: SearchController</p>
 * <p>Description: solr搜索服务</p>
 * <p>Company: www.compass.com</p> 
 * @author wkm
 * @date 2017年11月18日下午7:06:50
 * @version 2.0
 */
@Controller
public class SearchController {
	
	@Autowired
	private SearchService searchService;
	
	@RequestMapping(value = "search", method = RequestMethod.GET)
	public ModelAndView search(@RequestParam("q") String keyWords, 
			@RequestParam(value = "page", defaultValue = "1") Integer pageNum) { // 每页量由服务器定义
		
		ModelAndView mv = new ModelAndView("");
		
		try {
			keyWords = new String(keyWords.getBytes("ISO-8859-1"), "UTF-8"); // 处理GET中文编码
			SearchResult searchResult = this.searchService.search(keyWords, pageNum);
			int total = searchResult.getTotal().intValue();
			int pages = total % Constant.ROWS == 0 ? total / Constant.ROWS : total % Constant.ROWS + 1;
			
			mv.addObject("query", keyWords);
			mv.addObject("itemList", searchResult.getDataList());
			mv.addObject("page", pageNum); // 当前页
			mv.addObject("pages", pages); // 总页数
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
}
