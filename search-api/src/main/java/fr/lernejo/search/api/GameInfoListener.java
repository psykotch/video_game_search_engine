package fr.lernejo.search.api;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.Console;
import java.io.IOException;

@Component
public class GameInfoListener {

    private final RestHighLevelClient _highLevelClient;
    GameInfoListener(RestHighLevelClient highLevelClient) {
        this._highLevelClient = highLevelClient;
    }

    @RabbitListener(queues = "GAME_INFO_QUEUE")
    public void onMessage(String message, @Header("game_id") String id) throws IOException {
        IndexRequest indexRequest = new IndexRequest("games").id(id).source(message, XContentType.JSON);
        try {
            this._highLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        }
        catch (IOException exception){
            System.out.println("indexing error");
        }
    }
}
