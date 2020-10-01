package thealeshka.youtubebot.thred.handler;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import thealeshka.youtubebot.constants.Constants;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Thealeshka on 11.04.2020 inside the package - com.thealeshka.youtube_bot.thred_handler
 */

@Component
@Slf4j
public class ThreadHandler {
    @Getter
    private volatile ExecutorService threadPool;

    @PostConstruct
    void createThreadPool() {
        log.info("creating thread pool");
        threadPool = Executors.newFixedThreadPool(Constants.THREAD_COUNT);
        log.info("thread pool has been created");
    }
}
