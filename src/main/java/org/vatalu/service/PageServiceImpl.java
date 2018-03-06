package org.vatalu.service;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.vatalu.dao.PageRepository;
import org.vatalu.model.SHUPage;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

@Service
public class PageServiceImpl {
    @Autowired
    private PageRepository pageRepository;

    public Page<SHUPage> findPages(String keyword, int start, int size) {
        QueryBuilder queryBuilder = multiMatchQuery(keyword, "result.content", "result.title");
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withIndices("shu")
                .withTypes("page")
                .withHighlightFields(new HighlightBuilder.Field("result.title").fragmentSize(50),
                                    new HighlightBuilder.Field("result.content").fragmentSize(300).numOfFragments(3))
                .withPageable(PageRequest.of(start, size))
                .build();
        return pageRepository.search(searchQuery);
    }

}
