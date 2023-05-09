package laba3.winnerAttack;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

public class WinnerAttack {
    public Result makeAttack(BigInteger e, BigInteger n){
        BigInteger d=null;
        ArrayList<ChainShot> chainShots=new ArrayList<>();
        chainShots.add(new ChainShot(BigInteger.ZERO,BigInteger.ONE));
        BigInteger m=new BigInteger(n.bitLength()-1,new Random());
        BigInteger mE=m.modPow(e,n);
        ChainShot altha0=new ChainShot(e,n);
        BigInteger a0=makeCurrentA(altha0);
        ChainShot altha=nextAltha(altha0,a0);
        BigInteger a=makeCurrentA(altha);
        BigInteger minD=n.sqrt().sqrt().divide(BigInteger.valueOf(3));
        chainShots.add(new ChainShot(BigInteger.ONE,a));
        while(!(chainShots.get(chainShots.size()-1).getD().compareTo(minD)>0)){
            if(mE.modPow(chainShots.get(chainShots.size()-1).getD(),n).equals(m)){
                d=chainShots.get(chainShots.size()-1).getD();
            }
            altha=nextAltha(altha,a);
            a=makeCurrentA(altha);
            chainShots.add(nextChainShot(chainShots.get(chainShots.size()-1), chainShots.get(chainShots.size()-2),a));
        }
        return new Result(chainShots,d);
    }
    BigInteger makeCurrentA(ChainShot altha){
        return altha.getK().divide(altha.getD());
    }
    ChainShot nextAltha(ChainShot altha,BigInteger a){
        BigInteger d=altha.getK().subtract(a.multiply(altha.getD()));
        return new ChainShot(altha.getD(),d);
    }
    ChainShot nextChainShot(ChainShot chainShot1, ChainShot chainShot2,BigInteger a){
        BigInteger k=a.multiply(chainShot1.getK()).add(chainShot2.getK());
        BigInteger d=a.multiply(chainShot1.getD()).add(chainShot2.getD());
        return new ChainShot(k,d);
    }

}
