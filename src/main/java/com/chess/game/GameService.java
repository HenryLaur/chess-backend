package com.chess.game;

import com.chess.player.Player;
import com.chess.player.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;

    public Long getNextGame(Player player) {
        final Long[] gameId = {null};
        gameRepository.findAll().forEach(game -> {
            if(game.getPlayer2() == null) {
                player.setColor("black");
                game.setPlayer2(player);
                playerRepository.save(player);
                gameRepository.save(game);
                gameId[0] = game.getId();
            }
        });
        if (gameId[0] == null) {
            Game game = new Game();
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
            if(game.getPlayer1().getUuid().equals(playerUuid) || game.getPlayer2().getUuid().equals(playerUuid)) {
                gameId[0] = game.getId();
            }
        });
        return gameId[0];
    }
}
