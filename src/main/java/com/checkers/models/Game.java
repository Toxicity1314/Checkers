package com.checkers.models;

import com.checkers.models.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ColumnDefault("IN_PROGRESS")
    private Status status;

    @Column(name = "start_time")
    @NotNull
    private LocalDateTime startTime;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "gamesPlayed", fetch = FetchType.EAGER)//, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Player> gamePlayers;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private Player winner;

    @OneToMany(mappedBy="game", fetch = FetchType.EAGER)
    private Set<Piece> pieces;



    public Game() {
        this.startTime = LocalDateTime.now();
        this.status = Status.IN_PROGRESS;
        this.gamePlayers = new HashSet<>();
        this.pieces = new HashSet<>();
    }

    public long getId() {
        return this.id;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Player> getGamePlayers() {
        return this.gamePlayers;
    }

    public void addPlayer(Player player) {
        this.gamePlayers.add(player);
        player.getGamesPlayed().add(this);
    }
    public void addPiece(Piece piece) {
        this.pieces.add(piece);
    }

    public void removePlayer(Player player) {
        this.gamePlayers.remove(player);
        player.getGamesPlayed().remove(this);
    }



    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("game id ").append(getId()).append(" players: ");
        for (Player gamePlayer : gamePlayers) {
            stringBuilder.append(gamePlayer.getUsername()).append(", ");
        }
        return stringBuilder.toString();
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public Set<Piece> getPieces() {
        return pieces;
    }

    public void setPieces(Set<Piece> pieces) {
        this.pieces = pieces;
    }
}
