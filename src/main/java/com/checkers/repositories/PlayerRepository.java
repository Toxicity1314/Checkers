package com.checkers.repositories;

import com.checkers.securityConfiguration.SecurityUser;
import org.springframework.data.jpa.repository.JpaRepository;
import com.checkers.models.Player;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Transactional
    default Player patchPlayer(Long id, Player playerDTO) {
        Optional<Player> optionalPlayer = findById(id);
        if (optionalPlayer.isPresent()) {
            Player playerToUpdate = optionalPlayer.get();
            if (playerDTO.getName() != null)
                playerToUpdate.setName(playerDTO.getName());
            if (playerDTO.getEmail() != null)
                playerToUpdate.setEmail(playerDTO.getEmail());
            if (playerDTO.getUsername() != null)
                playerToUpdate.setUsername(playerDTO.getUsername());
            return save(playerToUpdate);
        }
        return null;
    }
    @Query(value = "SELECT id, username, password FROM players WHERE username = ?1", nativeQuery = true)
    Optional<SecurityUser> findByUsername(String username);

}
