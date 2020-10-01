package thealeshka.youtubebot.constants;

import java.util.Optional;

/**
 * Created by Thealeshka on 21.03.2020 inside the package - com.thealeshka.youtube_bot
 */


public final class Constants {

    public static final int RETRY_ON_FAIL = Integer.parseInt(Optional.ofNullable(System.getenv("RETRY_ON_FAIL")).orElse("2"));
    public static final String BOT_NAME = Optional.ofNullable(System.getenv("BOT_NAME")).orElse("YouTubeAudioBot_bot");
    public static final String BOT_TOKEN = Optional.ofNullable(System.getenv("BOT_TOKEN")).orElse("1392644887:AAFdU8tYqinYbvxIy4MPa7-PotUB1C_JqVM");
    public static final int THREAD_COUNT = Integer.parseInt(Optional.ofNullable(System.getenv("THREAD_COUNT")).orElse("16"));
    public static final int BUFFER_SIZE = Integer.parseInt(Optional.ofNullable(System.getenv("BUFFER_SIZE")).orElse("1024"));
    public static final int MAX_VIDEO_LENGTH = Integer.parseInt(Optional.ofNullable(System.getenv("MAX_VIDEO_LENGTH")).orElse("600"));

}
