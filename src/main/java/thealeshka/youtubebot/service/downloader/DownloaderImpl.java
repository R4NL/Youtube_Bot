package thealeshka.youtubebot.service.downloader;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.YoutubeVideo;
import com.github.kiulian.downloader.model.formats.AudioFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static thealeshka.youtubebot.constants.Constants.*;

@Component
@Slf4j
public class DownloaderImpl implements Downloader {
    private final YoutubeDownloader downloader;

    {
        downloader = new YoutubeDownloader();
        init();
    }

    private void init() {
        downloader.addCipherFunctionPattern(2, "\\b([a-zA-Z0-9$]{2})\\s*=\\s*function\\(\\s*a\\s*\\)\\s*\\{\\s*a\\s*=\\s*a\\.split\\(\\s*\"\"\\s*\\)");
        downloader.setParserRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        downloader.setParserRetryOnFailure(RETRY_ON_FAIL);
    }

    @Override
    public File download(AudioResult audioResult, File file) throws IOException, RuntimeException {
        if (audioResult.getLengthSeconds() <= MAX_VIDEO_LENGTH) {
            try (BufferedInputStream in = new BufferedInputStream(audioResult.getLink().openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(file.getPath())) {
                byte[] dataBuffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, BUFFER_SIZE)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
                return file;
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw e;
            }
        } else {
            log.error("Too long video!");
            throw new IllegalArgumentException("Too long video!");
        }
    }

    @Override
    public AudioResult getAudioResult(String videoId) throws YoutubeException, IOException, URISyntaxException {
        try {
            YoutubeVideo video = downloader.getVideo(getVideoId(videoId));
            List<AudioFormat> audioFormats = new ArrayList<>(video.audioFormats());
            audioFormats.sort(Comparator.comparing(AudioFormat::averageBitrate));
            return generateResult(audioFormats.get(audioFormats.size() - 2), video);
        } catch (YoutubeException.BadPageException e) {
            log.error(e.getMessage(), e);
            throw  new IllegalArgumentException("Wrong video link!");
        } catch (YoutubeException | IOException | URISyntaxException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    private AudioResult generateResult(AudioFormat audioFormat, YoutubeVideo video) throws MalformedURLException {
        try {
            return new AudioResult().toBuilder()
                    .title(video.details().title())
                    .author(video.details().author())
                    .lengthSeconds(video.details().lengthSeconds())
                    .link(new URL(audioFormat.url()))
                    .imageLink(new URL(video.details().thumbnails().get(video.details().thumbnails().size() - 1))).build();
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    private String getVideoId(String link) throws URISyntaxException {
        if (link != null && !link.isBlank()) {
            String result = "";
            log.info("video link {}", link);
            if (link.contains("?")) {
                result = URLEncodedUtils.parse(new URI(link), StandardCharsets.UTF_8).stream()
                        .collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue)).get("v");
            } else {
                result = link.substring(link.lastIndexOf("/") + 1);
            }
            log.info("video id {}", result);
            return result;
        } else throw new IllegalArgumentException("Get empty link!");
    }


}
