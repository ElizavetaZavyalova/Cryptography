package laba3.winnerAttack;

import lombok.*;

import java.math.BigInteger;
import java.util.ArrayList;
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Result {
    ArrayList<ChainShot> chainShots;
    BigInteger d;
}
