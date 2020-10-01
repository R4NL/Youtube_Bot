package thealeshka.youtubebot.service.executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import thealeshka.youtubebot.bot.BotContext;
import thealeshka.youtubebot.service.downloader.AudioResult;
import thealeshka.youtubebot.service.downloader.Downloader;
import thealeshka.youtubebot.thred.handler.ThreadHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class ExecutorImpl implements Executor {

    @Autowired
    ThreadHandler threadHandler;

    @Autowired
    Downloader downloader;

    @Override
    public void executeTask(String videoId, BotContext context) {
        CompletableFuture.runAsync(() -> {
            try {
                sendAudio(download(getAudioResult(videoId, context), context), context);
            } catch (IllegalArgumentException e) {
                log.error(e.getMessage(), e);
                sendMessage(e.getMessage(), context);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sendMessage("Server error!", context);
            }
        }, threadHandler.getThreadPool());
    }

    @Override
    public Integer sendMessage(String text, BotContext context) {
        try {
            SendMessage message = new SendMessage()
                    .setChatId(context.getUser().getChatId())
                    .setText(text);
            Integer result = context.getBot().execute(message).getMessageId();
            log.info("Send message text {} id {}", text, result);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private void deleteMessage(Integer id, BotContext context) {
        try {
            log.info("Delete message id {}", id);
            DeleteMessage message = new DeleteMessage()
                    .setChatId(context.getUser().getChatId())
                    .setMessageId(id);
            context.getBot().execute(message);
        } catch (TelegramApiException e) {
            log.error("deleting message", e);
        }
    }

    private AudioResult getAudioResult(String videoId, BotContext context) throws Exception {
        Integer messageId = null;
        try {
            messageId = sendMessage("Getting video info)", context);
            AudioResult result = downloader.getAudioResult(videoId);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            deleteMessage(messageId, context);
        }
    }

    private File download(AudioResult audioResult, BotContext context) throws Exception {
        Integer messageId = null;
        try {
            messageId = sendMessage("downloading audio)", context);
            File audio = createFile("my_videos/" + Thread.currentThread().getName() + "/" + audioResult.getTitle() + ".mp3");
            File file = downloader.download(audioResult, audio);
            return file;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            deleteMessage(messageId, context);
        }
    }

    private Message sendAudio(File file, BotContext context) throws Exception {
        Integer messageId = null;
        try {
            messageId = sendMessage("sending audio)", context);
            if (file == null || !file.exists() || !file.canRead() || !file.isFile()) {
                log.error("wrong file");
                throw new IllegalArgumentException("wrong file");
            }
            SendAudio message = new SendAudio().setAudio(file).setChatId(context.getUser().getChatId());
            Message result = context.getBot().execute(message);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            deleteFile(file.toPath());
            deleteMessage(messageId, context);
        }
    }

    private void deleteFile(Path path) {
        path.toFile().delete();
        log.info("delete mp3");
    }

    private File createFile(String path) throws IOException {
        File file = Path.of(path).toFile();
        try {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return file;
    }


}


