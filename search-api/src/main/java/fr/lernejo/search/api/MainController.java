package fr.lernejo.search.api;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;

@RestController
public class MainController {
    private final RestHighLevelClient _highLevelClient;

    public MainController(RestHighLevelClient highLevelClient) {
        this._highLevelClient = highLevelClient;
    }

    @GetMapping("/api/games")
    public ArrayList<Object> getGames(@RequestParam(name = "query") String query) throws IOException {
        ArrayList<Object> list = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest()
            .source(SearchSourceBuilder.searchSource().query(new QueryStringQueryBuilder(query)));
        this._highLevelClient.search(searchRequest, RequestOptions.DEFAULT)
            .getHits().forEach(hit -> list.add(hit.getSourceAsMap()));
        return list;
    }
}
