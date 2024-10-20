package com.checkers.service;

import com.checkers.dtos.GameDTO;
import com.checkers.dtos.MoveDTO;
import com.checkers.models.Game;
import com.checkers.models.GamePlayer;
import com.checkers.models.Piece;
import com.checkers.models.Player;
import com.checkers.models.enums.PieceColor;
import com.checkers.repositories.GameRepository;
import com.checkers.repositories.PieceRepository;
import com.checkers.repositories.PlayerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.*;

@Service
public class GameService {
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final PieceRepository pieceRepository;

    public GameService(PlayerRepository playerRepository, GameRepository gameRepository,
            PieceRepository pieceRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.pieceRepository = pieceRepository;
    }

    @Transactional
    public GameDTO makeMove(MoveDTO move, Principal principal) {
        Player playerAttemptingMove = playerRepository.findPlayerByUsername(principal.getName());
        if (move.getPlayerId() != playerAttemptingMove.getId())
            throw new IllegalArgumentException("not your piece man");
        Game game = gameRepository.findById(move.getGameId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Game with id " + move.getGameId() + " not found."));

        List<Piece> piecesToRemove = validateMove(game, move);
        if (piecesToRemove != null) {
            pieceRepository.movePiece(move.getPieceId(), move.getDestinationRow(), move.getDestinationColumn());
            game = removePieces(piecesToRemove, move.getGameId());
        } else {
            System.out.println("Illegal move bruh");
        }

        Set<GamePlayer> gamePlayers = game.getGamePlayers();
        for (GamePlayer gamePlayer : gamePlayers) {
            if (gamePlayer.getPlayer().getUsername() != game.getPlayerTurn().getUsername()) {
                game.setPlayerTurn(gamePlayer.getPlayer());
                break;
            }
        }
        // switches what players turn it is. ENDED HERE START BY CHECKING WHAT
        // INFROMATION IS PASSED BACK IN MOVE DTO
        return new GameDTO(game);

    }

    private Game removePieces(List<Piece> piecesToRemove, Long gameId) {
        try {
            Game game = gameRepository.findById(gameId)
                    .orElseThrow(() -> new IllegalArgumentException("Game not found"));

            for (Piece pieceToRemove : piecesToRemove) {
                System.out.println("Jump");
                if (pieceRepository.existsById(pieceToRemove.getId())) {
                    game.getPieces().remove(pieceToRemove);
                    pieceRepository.delete(pieceToRemove);
                } else {
                    throw new EntityNotFoundException("Piece with ID " + pieceToRemove.getId() + " not found.");
                }
            }

            // Fetch the updated game with pieces as a projection
            return gameRepository.findById(gameId).orElseThrow(() -> new IllegalArgumentException("Game not found"));
        } catch (IllegalArgumentException e) {
            System.out.println("heeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeellllo");
            return null;
        }
    }

    public List<Piece> validateMove(Game game, MoveDTO move) throws IllegalArgumentException {
        Piece piece = pieceRepository.findById(move.getPieceId())
                .orElseThrow(() -> new IllegalArgumentException("piece not found"));

        if (!move.getPlayerId().equals(game.getPlayerTurn().getId())) {
            throw new IllegalArgumentException("Please wait for your turn");
        }

        if (!isDiagonalAndOnBoard(move)) {
            throw new IllegalArgumentException("Move is not diagonal or out of board bounds");
        }

        if (!isCorrectDirection(move, piece)) {
            throw new IllegalArgumentException("Incorrect movement direction for the piece");
        }

        if (!isSpaceEmpty(game, move)) {
            throw new IllegalArgumentException("Destination space is not empty");
        }
        boolean isJump = isJump(move);

        if (isJump) {
            return findPiecesJumped(game, move, piece);
        } else {
            return Collections.emptyList();
        }
    }

    public boolean isCorrectDirection(MoveDTO move, Piece piece) {
        return piece.isKing() ||
                (piece.getColor() == PieceColor.BLACK && move.getSourceRow() - move.getDestinationRow() > 0) ||
                (piece.getColor() == PieceColor.RED && move.getSourceRow() - move.getDestinationRow() < 0);
    }

    public boolean isDiagonalAndOnBoard(MoveDTO move) {
        int rowDistance = move.getDestinationRow() - move.getSourceRow();
        int columnDistance = move.getDestinationColumn() - move.getSourceColumn();
        boolean isDiagonal = (Math.abs(rowDistance) == Math.abs(columnDistance));

        return isDiagonal && move.getDestinationColumn() <= 7 && move.getDestinationRow() <= 7 &&
                move.getDestinationColumn() >= 0 && move.getDestinationRow() >= 0;
    }

    public boolean isSpaceEmpty(Game game, MoveDTO move) {
        Optional<Piece> rowAndColumnCheck = game.getPieces().stream()
                .filter(checkPiece -> checkPiece.getColumn() == move.getDestinationColumn()
                        && checkPiece.getRow() == move.getDestinationRow())
                .findAny();
        return rowAndColumnCheck.isEmpty();
    }

    public boolean isJump(MoveDTO move) {
        return Math.abs(move.getSourceRow() - move.getDestinationRow()) > 1 &&
                Math.abs(move.getSourceColumn() - move.getDestinationColumn()) > 1;
    }

    public List<Piece> findPiecesJumped(Game game, MoveDTO move, Piece piece) {
        List<Piece> piecesToDelete = new ArrayList<>();
        int rowOffSet = piece.getColor() == PieceColor.RED ? 1 : -1;
        int columnOffSet = move.getDestinationColumn() - move.getSourceColumn() > 0 ? 1 : -1;
        int rowToCheck = move.getSourceRow() + rowOffSet;
        int columnToCheck = move.getSourceColumn() + columnOffSet;

        Optional<Piece> previousRowAndColumnCheck = game.getPieces().stream()
                .filter(checkPiece -> checkPiece.getColumn() == move.getSourceColumn() + columnOffSet
                        && checkPiece.getRow() == move.getSourceRow() + rowOffSet)
                .findAny();

        if (previousRowAndColumnCheck.isPresent() && previousRowAndColumnCheck.get().getColor() != piece.getColor()) {
            boolean legalJump = true;
            do {
                rowToCheck += rowOffSet;
                columnToCheck += columnOffSet;

                int finalColumnToCheck = columnToCheck;
                int finalRowToCheck = rowToCheck;
                Optional<Piece> currentRowAndColumnCheck = game.getPieces().stream()
                        .filter(checkPiece -> checkPiece.getColumn() == finalColumnToCheck
                                && checkPiece.getRow() == finalRowToCheck)
                        .findAny();
                if ((currentRowAndColumnCheck.isPresent() == previousRowAndColumnCheck.isPresent()) ||
                        (currentRowAndColumnCheck.isPresent()
                                && currentRowAndColumnCheck.get().getColor() == piece.getColor())) {

                    return null;
                }
                previousRowAndColumnCheck.ifPresent(piecesToDelete::add);
                previousRowAndColumnCheck = currentRowAndColumnCheck;
            } while (rowToCheck != move.getDestinationRow() && columnToCheck != move.getDestinationColumn());

        }
        // if(piecesToDelete.isEmpty()) return null;
        return piecesToDelete;

    }
}
