package com.chess.player;

import com.chess.game.Game;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Player {
    @Id
    private String uuid;
    private String color;
}
