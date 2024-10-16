package com.checkers.controllers;

import com.checkers.dtos.GameDTO;
import com.checkers.dtos.MoveDTO;
import com.checkers.service.GameService;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    private final GameService gameService;

    public WebSocketController(GameService gameService) {
        this.gameService = gameService;
    }

    @MessageMapping("/game/{gameId}")
    @SendTo("/topic/{gameId}")
    public GameDTO makeMove(MoveDTO move, Principal principal) {

        System.out.println(" user is " + principal.getName());
        return gameService.makeMove(move, principal);
    }

    @MessageExceptionHandler(IllegalArgumentException.class)
    @SendToUser(value = "/queue/errors", broadcast = false)
    public String handleException(IllegalArgumentException exception) {
        return exception.getMessage();
    }

}
