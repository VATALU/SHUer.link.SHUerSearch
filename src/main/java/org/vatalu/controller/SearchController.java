package org.vatalu.controller;

import org.vatalu.client.ElasticsearchClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
public class SearchController {
    @Resource
    private ElasticsearchClient elasticsearchClient;

    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public List<Map<String,String>> greeting(@RequestParam(value = "q") String keyword,
                                             @RequestParam(value = "start", defaultValue = "0", required = false) Integer start,
                                             @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        if (keyword != null && !keyword.equals("")) {
            return elasticsearchClient.getFullTextQuery(keyword, start, size);
        } else {
            return null;
        }
    }

}
