package Coursework.LUC;

import Coursework.LUC.tests.FarmTest;
import Coursework.LUC.tests.MillerRabinTest;
import Coursework.LUC.tests.SimplicityCheck;
import Coursework.LUC.tests.SolovayStrassenTest;
import lombok.Setter;
import Coursework.LUC.tests.*;

import java.math.BigInteger;
import java.util.*;

public class LUC {
    static class GenerateKeys {
        Map<SimplicityMode, SimplicityCheck> simplicityModes = new HashMap<>();
        Random random=new Random();
        @Setter
        SimplicityMode testMode;
        @Setter
        int bitLength;
        @Setter
        float probability;
        public GenerateKeys(SimplicityMode testMode, float probability, int biteLength){
            setProbability(probability);
            setTestMode(testMode);
            setBitLength(biteLength);
            simplicityModes.put(SimplicityMode.farm,new FarmTest());
            simplicityModes.put(SimplicityMode.millerRabin,new MillerRabinTest());
            simplicityModes.put(SimplicityMode.solovayStrassen,new SolovayStrassenTest());

        }
        BigInteger generateBigInteger(){
            return BigInteger.probablePrime(bitLength,random);
            //return new BigInteger(bitLength,random).setBit(bitLength-1).setBit(0);
        }
        boolean isBigIntegerSizeCorrect(BigInteger number){
            return number.bitLength()==bitLength;
        }


        BigInteger generateSimpleBigInteger(BigInteger generate){
            while(!simplicityModes.get(testMode).isSimple(generate,probability)){
                generate=generate.add(BigInteger.TWO);
                if(!isBigIntegerSizeCorrect(generate)){
                    generate=BigInteger.ONE.shiftLeft(bitLength-1).setBit(0);
                }
            }
            return generate;
        }
        public LUCKey generateLUCKey(){
            LUCKey lucKey=new LUCKey();
            BigInteger p=generateSimpleBigInteger(generateBigInteger());
            int addLength= random.nextInt(bitLength>>1,(bitLength-1));
            BigInteger addNum=new BigInteger(addLength,random).setBit(addLength>>2-1).clearBit(0);
            BigInteger q=generateSimpleBigInteger(p.add(addNum));
            lucKey.setE(makeE(p, q));
            lucKey.setN(p.multiply(q));
            lucKey.d.setQ(q);
            lucKey.d.setP(p);
            lucKey.d.make(lucKey.getE());
            return  lucKey;
        }
        BigInteger makeMul(BigInteger p, BigInteger q){
            BigInteger mul1=p.add(BigInteger.ONE);
            BigInteger mul2=q.add(BigInteger.ONE);
            BigInteger mul3=p.subtract(BigInteger.ONE);
            BigInteger mul4=q.subtract(BigInteger.ONE);
            return mul1.multiply(mul2).multiply(mul3).multiply(mul4);
        }
        BigInteger makeE(BigInteger p, BigInteger q){
            BigInteger mul=makeMul(p,q);
            BigInteger e=generateE(p.bitLength());
            while(mul.gcd(e).compareTo(BigInteger.ONE)!=0){ //gcd(e,(p^2-1)(q^2-1)!=1
                e=generateE(p.bitLength());
            }
            return e;
        }
        public BigInteger generateE(int len){
            int oneCount= random.nextInt(1,7);
            BigInteger e=BigInteger.ONE.shiftLeft(len).setBit(0);
            for(int i=0; i<oneCount; i++) {
                e = e.setBit(random.nextInt(2, len- 1));
            }
            return e;
        }
    }



    GenerateKeys generateKeys=null;
    public LUC(SimplicityMode testMode, float probability, int biteLength){
        generateKeys=new GenerateKeys(testMode,probability,biteLength);
    }
    LUCKey lucKey=null;
    public LUCKey generateKey(){
       lucKey=generateKeys.generateLUCKey();
        return lucKey;
    }


}
