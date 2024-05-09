package com.checkers.controllers;

import com.checkers.dtos.GameDTO;
import com.checkers.models.Game;
import com.checkers.repositories.GameRepository;
import com.checkers.service.GameCreationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/game")
public class GameController {
    private final GameRepository gameRepository;
    private final GameCreationService gameCreationService;

    public GameController(GameRepository gameRepository, GameCreationService gameCreationService) {
        this.gameRepository = gameRepository;
        this.gameCreationService = gameCreationService;
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameDTO> getGame(@PathVariable Long gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with id " + gameId + " not found."));
        return ResponseEntity.status(HttpStatus.OK).body(new GameDTO(game));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<GameDTO> createGame(@RequestBody @NotNull CreateGameRequest createGameRequest,
            HttpServletRequest request) {
        Game newGame = gameCreationService.createGame(createGameRequest.player1_id, createGameRequest.player2_id);
        return ResponseEntity.status(HttpStatus.CREATED).body(new GameDTO(newGame));
    }

    public record CreateGameRequest(long player1_id, long player2_id) {}
}