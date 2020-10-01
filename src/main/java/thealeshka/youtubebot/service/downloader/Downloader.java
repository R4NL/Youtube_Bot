package thealeshka.youtubebot.service.downloader;

import com.github.kiulian.downloader.YoutubeException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public interface Downloader {
    AudioResult getAudioResult(String videoId) throws YoutubeException, IOException, URISyntaxException;

    File download(AudioResult audioResult, File file) throws IOException;
}
