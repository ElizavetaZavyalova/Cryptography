package laba3.tests;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Random;
@AllArgsConstructor
public abstract class SimplicityTest implements SimplicityCheck{
    Random rand;
    private final float probabilityOfTest;
    @Override
    public boolean isSimple(BigInteger number, float probability) {
        float result=1;
        do{
            if(isNumberSimple(number)){
                result*=probabilityOfTest;
            }
            else{
                return false;
            }
        }while ((1.0F-result)<probability);
        return true;
    }
    public BigInteger nextRandomBigInteger(int min, BigInteger max) {
        BigInteger result = new BigInteger(max.bitLength(), rand);
        while(result.compareTo(max) >= 0 || result.compareTo(BigInteger.valueOf(min))<0) {
            result = new BigInteger(max.bitLength(), rand);
        }
        return result;
    }
    abstract boolean isNumberSimple(BigInteger number);


}
