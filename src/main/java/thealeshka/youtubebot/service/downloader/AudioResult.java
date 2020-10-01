package thealeshka.youtubebot.service.downloader;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class AudioResult {
    private String title;
    private String author;
    private Integer lengthSeconds;
    private URL link;
    private URL imageLink;
}
