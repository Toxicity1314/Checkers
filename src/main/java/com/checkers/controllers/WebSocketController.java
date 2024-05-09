package com.checkers.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @MessageMapping("/game/{gameId}")
    @SendTo("/topic/{gameId}")
    public String makeMove(String message){
        System.out.println("I made it to makeMove" + message);
        return message;
    }
}
