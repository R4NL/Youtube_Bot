package thealeshka.youtubebot.bot;


import lombok.Data;
import thealeshka.youtubebot.bot.Bot;
import thealeshka.youtubebot.service.User;

/**
 * Created by Thealeshka on 21.03.2020 inside the package - com.thealeshka.youtube_bot.bot
 */

@Data
public final class BotContext {
    private final Bot bot;
    private final User user;
    private final String input;

    public static BotContext of(Bot bot, User user, String text) {
        return new BotContext(bot, user, text);
    }
}
