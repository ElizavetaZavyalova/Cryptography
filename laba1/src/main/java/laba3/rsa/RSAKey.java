package laba3.rsa;

import lombok.*;

import java.math.BigInteger;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class RSAKey {
    BigInteger e;
    BigInteger d;
    BigInteger n;
    BigInteger thi;
}
