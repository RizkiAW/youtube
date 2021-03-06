package com.veerayaaa.youtubetomp3bot;

import com.veerayaaa.youtubetomp3bot.controller.TelegramBotController;
import com.veerayaaa.youtubetomp3bot.model.ConversionWorkUnit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@SpringBootApplication
public class YouTubeToMp3BotApp implements ApplicationListener<ContextRefreshedEvent> {

	public static Path downloadedContentDir;
	public static BlockingQueue<ConversionWorkUnit> conversionQueue;

	private static final String TMP_DOWNLOADED_VID_PATH = "/tmp/downloadedVideo";

	public static void main(String[] args) throws IOException {
		initializeDownloadPath();
		conversionQueue = new ArrayBlockingQueue<>(10);
		ApiContextInitializer.init();

		SpringApplication.run(YouTubeToMp3BotApp.class, args);
	}

	private static void initializeDownloadPath() {
		File dir = new File(TMP_DOWNLOADED_VID_PATH);
		dir.mkdir();
		dir.setWritable(true, false);
		dir.setReadable(true, false);
		downloadedContentDir = Paths.get(TMP_DOWNLOADED_VID_PATH);
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		initializeTelegramBot(contextRefreshedEvent.getApplicationContext());
	}

	private void initializeTelegramBot(ApplicationContext context) {
		TelegramBotController telegramBotController = context.getBean(TelegramBotController.class);
		try {
			TelegramBotsApi botsApi = new TelegramBotsApi();
			botsApi.registerBot(telegramBotController);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}
