package fr.lernejo.fileinjector;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Launcher {
    public record GameInfo(
        int id,
        String title,
        String thumbnail,
        String short_description,
        String game_url,
        String genre,
        String platform,
        String publisher,
        String developer,
        String release_date,
        String freetogame_profile_url
    ) {
    }

    public static void main(String[] args) {
        try (AbstractApplicationContext springContext = new AnnotationConfigApplicationContext(Launcher.class)) {
            ObjectMapper mapper = new ObjectMapper();
            List<GameInfo> gameInfos = Arrays.asList(mapper.readValue(Paths.get(args[0]).toFile(), GameInfo[].class));
            RabbitTemplate rabbitTemplate = springContext.getBean(RabbitTemplate.class);
            for (GameInfo gameInfo : gameInfos) {
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                rabbitTemplate.convertAndSend("", "game_info", gameInfo, message -> {
                    message.getMessageProperties().getHeaders().put("game_id", gameInfo.id());
                    return message;
                });
            }
        } catch (StreamReadException e) {
            throw new RuntimeException(e);
        } catch (DatabindException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
