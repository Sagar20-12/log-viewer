package com.example.logviewer.service;

import com.example.logviewer.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class FileWatcherService {
    private final static String FILE_NAME = "log.txt";
    public static final String DESTINATION = "/topic/log";
    private long lastModified = 0;
    private long lastFileSize = 0;
    private List<String> lastLines = new ArrayList<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Constructor to initialize the service
    public FileWatcherService() {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();
            }
            // Initialize tracking variables
            lastModified = file.lastModified();
            lastFileSize = file.length();
            // Load initial last 10 lines
            loadLastLines();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedDelay = 500, initialDelay = 1000) // Check every 500ms for better responsiveness
    public void checkForUpdates() {
        try {
            File file = new File(FILE_NAME);
            long currentModified = file.lastModified();
            long currentSize = file.length();

            // Check if file was modified and size changed
            if (currentModified > lastModified || currentSize != lastFileSize) {
                System.out.println("File change detected. Size: " + currentSize + ", LastModified: " + currentModified);
                lastModified = currentModified;
                lastFileSize = currentSize;
                loadLastLines();
                sendLastLines();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLastLines() throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0) {
            lastLines = new ArrayList<>();
            return;
        }

        // Use more efficient approach for large files
        lastLines = readLastNLines(file, 10);
    }

    private List<String> readLastNLines(File file, int n) throws IOException {
        List<String> result = new ArrayList<>();

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            long fileLength = randomAccessFile.length();
            if (fileLength == 0) {
                return result;
            }

            // Start from the end of the file
            long pointer = fileLength - 1;
            List<String> lines = new ArrayList<>();
            StringBuilder currentLine = new StringBuilder();

            // Read backwards
            while (pointer >= 0 && lines.size() < n) {
                randomAccessFile.seek(pointer);
                char c = (char) randomAccessFile.read();

                if (c == '\n' || c == '\r') {
                    if (currentLine.length() > 0) {
                        lines.add(currentLine.reverse().toString());
                        currentLine = new StringBuilder();
                    }
                } else {
                    currentLine.append(c);
                }
                pointer--;
            }

            // Add the last line if we reached the beginning of file
            if (currentLine.length() > 0) {
                lines.add(currentLine.reverse().toString());
            }

            // Reverse the order to get chronological order
            Collections.reverse(lines);

            // Remove empty lines
            return lines.stream()
                    .filter(line -> !line.trim().isEmpty())
                    .collect(ArrayList::new, (list, item) -> list.add(item), ArrayList::addAll);
        }
    }

    private void sendLastLines() {
        System.out.println("Sending " + lastLines.size() + " lines to frontend");

        // Send clear message first
        Message clearMessage = new Message();
        clearMessage.setContent("CLEAR");
        messagingTemplate.convertAndSend(DESTINATION, clearMessage);

        // Send each line
        for (String line : lastLines) {
            if (!line.trim().isEmpty()) {
                Message message = new Message();
                message.setContent(line.trim());
                messagingTemplate.convertAndSend(DESTINATION, message);
            }
        }
    }

    // Method to get current last lines for initial load
    public List<String> getCurrentLastLines() {
        try {
            loadLastLines();
            return new ArrayList<>(lastLines);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}