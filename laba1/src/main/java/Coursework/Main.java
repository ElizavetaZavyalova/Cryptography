package Coursework;


import Coursework.LUC.LUCRecurent;
import Coursework.LUC.LUCKey;
import Coursework.LUC.SimplicityMode;
import Coursework.LUC.LUC;
import Coursework.Twofish.RoundKeysForTwofish;
import Coursework.Twofish.Twofish;
import Coursework.Twofish.TwofishRound;
import org.algoritms.help.DebugFunctions;

import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {

        byte[] k={(byte)0x59, (byte)0x41, (byte)0x44, (byte)0x41, (byte)0x30,
                  (byte)0x36, (byte)0x31, (byte)0x33, (byte)0x50, (byte)0x4a,
                  (byte)0x54, (byte)0x59, (byte)0x32, (byte)0x5a, (byte)0x30,
                  (byte)0x53,(byte)0x59, (byte)0x41, (byte)0x44, (byte)0x41,
                  (byte)0x30, (byte)0x36, (byte)0x31, (byte)0x33};
        byte[] t={(byte)0xAc,(byte)0xB8,(byte)0xC4,(byte)0xF0,
                (byte)0xFd,(byte)0xF9,(byte)0xF5,(byte)0xF1,
                (byte)0xFe,(byte)0xFa,(byte)0xF6,(byte)0xF2,
                (byte)0xFf,(byte)0xFb,(byte)0xF7,(byte)0xF3};
        Twofish twofish=new Twofish(new TwofishRound(),new RoundKeysForTwofish());
        twofish.makeRoundKeys(k);
        DebugFunctions.debugByteHexArray(t);
        t=twofish.encrypt(t);
        DebugFunctions.debugByteHexArray(t);
        t=twofish.decrypt(t);
        DebugFunctions.debugByteHexArray(t);
       System.out.println(BigInteger.valueOf(-1));
        BigInteger massage= BigInteger.valueOf(11111);
        LUC luc=new LUC(SimplicityMode.farm, 0.99F,30);
        LUCKey key=luc.generateKey();
        System.out.println(key);
        System.out.println(massage);
        massage=(LUCRecurent.v(massage ,key.getE(),key.getN()));
        System.out.println(massage);
        BigInteger s=key.getD().getD(massage);
        System.out.println("s:"+s);
        BigInteger massage5=(LUCRecurent.v(massage,s,key.getN()));
        System.out.println(massage5);
        BigInteger massage1=(LUCRecurent.v(massage,key.getD().getP_qx(),key.getN()));
        System.out.println(massage1);
        BigInteger massage2=(LUCRecurent.v(massage,key.getD().getP_q_(),key.getN()));
        System.out.println(massage2);
        BigInteger massage3=(LUCRecurent.v(massage,key.getD().getPxq_(),key.getN()));
        System.out.println(massage3);
        BigInteger massage4=(LUCRecurent.v(massage,key.getD().getPxqx(),key.getN()));
        System.out.println(massage4);



    }
}
