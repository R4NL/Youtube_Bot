package thealeshka.youtubebot.bot;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import thealeshka.youtubebot.service.User;
import thealeshka.youtubebot.service.executor.Executor;
import thealeshka.youtubebot.thred.handler.ThreadHandler;

import java.util.Objects;

import static thealeshka.youtubebot.constants.Constants.BOT_NAME;
import static thealeshka.youtubebot.constants.Constants.BOT_TOKEN;
import static thealeshka.youtubebot.constants.ErrorsConstants.GET_UNEXPECTED_MESSAGE;
import static thealeshka.youtubebot.constants.OperationConstants.START_COMMAND;
import static thealeshka.youtubebot.constants.OperationConstants.START_MESSAGE;


/**
 * Created by Thealeshka on 21.03.2020 inside the package - com.thealeshka.youtube_bot.bot
 */


@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {

    @Autowired
    ThreadHandler threadHandler;

    @Autowired
    Executor executor;

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Get message");
        User user = getUser(update);
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = getTestMessage(update);
            log.info("Get text message {}", message);
            if (Objects.equals(message, START_COMMAND))
                start(user);
            else {
                executor.executeTask(message, BotContext.of(this, user, message));
                log.info("Add job to thread pool");
            }
        } else {
            log.error(GET_UNEXPECTED_MESSAGE + " {}", update);
            executor.sendMessage(GET_UNEXPECTED_MESSAGE, BotContext.of(this, user, null));
            throw new IllegalArgumentException(GET_UNEXPECTED_MESSAGE);
        }
    }

    private User getUser(Update update) {
        return new User(update.getMessage().getChatId());
    }

    //TODO add validation
    private String getTestMessage(Update update) {
        if (update == null || update.getMessage() == null || update.getMessage().getText() == null || update.getMessage().getText().isBlank()) {
            log.error(GET_UNEXPECTED_MESSAGE);
            throw new IllegalArgumentException(GET_UNEXPECTED_MESSAGE);
        }
        return update.getMessage().getText();
    }

    private void start(User user) {
        executor.sendMessage(START_MESSAGE, BotContext.of(this, user, null));
    }


    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}
