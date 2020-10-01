package thealeshka.youtubebot.service.executor;

import thealeshka.youtubebot.bot.BotContext;

public interface Executor {
    void executeTask(String videoId, BotContext context);

    Integer sendMessage(String text, BotContext context);
}
