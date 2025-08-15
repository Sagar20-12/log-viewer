package com.example.logviewer.controller;

import com.example.logviewer.model.Message;
import com.example.logviewer.service.FileWatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class FileWatcherController {

    @Autowired
    private FileWatcherService fileWatcherService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/logs")
    @SendTo("/topic/log")
    public Message getFileUpdates(Message message) {
        return message;
    }

    // Add endpoint to get initial logs when page loads
    @MessageMapping("/initial")
    public void getInitialLogs() {
        List<String> currentLines = fileWatcherService.getCurrentLastLines();

        // Send clear message first
        Message clearMessage = new Message();
        clearMessage.setContent("CLEAR");
        messagingTemplate.convertAndSend("/topic/log", clearMessage);

        // Send each line
        for (String line : currentLines) {
            if (!line.trim().isEmpty()) {
                Message message = new Message();
                message.setContent(line.trim());
                messagingTemplate.convertAndSend("/topic/log", message);
            }
        }
    }

    // REST endpoint for debugging
    @GetMapping("/api/logs")
    @ResponseBody
    public List<String> getCurrentLogs() {
        return fileWatcherService.getCurrentLastLines();
    }

    // Test endpoint to verify server is running
    @GetMapping("/api/test")
    @ResponseBody
    public String test() {
        return "Server is running! Time: " + java.time.LocalDateTime.now();
    }
}