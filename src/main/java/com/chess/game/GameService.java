package com.chess.game;

import com.chess.player.Player;
import com.chess.player.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;

    public Long getNextGame(Player player) {
        final Long[] gameId = {null};
        gameRepository.findAll().forEach(game -> {
            if(game.getPlayer2() == null && game.isActive()) {
                player.setColor("black");
                game.setPlayer2(player);
                playerRepository.save(player);
                gameRepository.save(game);
                gameId[0] = game.getId();
            }
        });
        if (gameId[0] == null) {
            Game game = new Game();
            game.setActive(true);
            player.setColor("white");
            game.setPlayer1(player);
            playerRepository.save(player);
            gameRepository.save(game);
            gameId[0] = getGameIdByPlayerUuid(player.getUuid());
        }
        return gameId[0];
    }

    public Game getGameById(Long gameId) {
        return gameRepository.findById(gameId).orElse(null);
    }

    public Long getGameIdByPlayerUuid(String playerUuid) {
        final Long[] gameId = {null};
        gameRepository.findAll().forEach( game -> {
            if(game.getPlayer1() != null && game.getPlayer1().getUuid().equals(playerUuid) || game.getPlayer2() != null && game.getPlayer2().getUuid().equals(playerUuid)) {
                gameId[0] = game.getId();
            }
        });
        return gameId[0];
    }
    public void setGameNotActiveByPlayerId(String playerUuid) {
        Optional<Game> gameOptional = gameRepository.findById(getGameIdByPlayerUuid(playerUuid));
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            game.setActive(false);
            gameRepository.save(game);
        }
    }
}
