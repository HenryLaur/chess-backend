package com.chess.game;

import com.chess.player.Player;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Game {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "ID")
    @JsonIgnore
    private Long id;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_1")
    private Player player1;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_2", nullable = true)
    private Player player2;

}
