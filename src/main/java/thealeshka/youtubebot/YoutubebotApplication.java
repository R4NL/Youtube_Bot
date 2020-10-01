package thealeshka.youtubebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;


@SpringBootApplication
public class YoutubebotApplication {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(YoutubebotApplication.class, args);
    }

}
