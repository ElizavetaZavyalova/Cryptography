package org.algoritms;


import org.algoritms.encryption.BaseEncryption;
import org.algoritms.encryption.des.FeistelNetwork;
import org.algoritms.encryption.rijndel.RijndaelRound;
import org.algoritms.encryption.des.Des;
import org.algoritms.encryption.rijndel.Rijndael;
import org.algoritms.help.DebugFunctions;
import org.algoritms.encryption.des.RoundKeysForDes;
import org.algoritms.encryption.rijndel.RoundKeysForRijndael;
import org.algoritms.modes.BaseModeEncryption;
import org.algoritms.modes.Settings.padding.PaddingPKCS7;
import org.algoritms.modes.enums.EncryptionType;
import org.algoritms.modes.enums.Mode;
import org.algoritms.modes.enums.PaddingType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    static void testDes(){
        byte[] key={(byte)0xAA,(byte)0xBB,(byte)0x09,(byte)0x18,(byte)0x27,(byte)0x36,(byte)0xCC};
        byte[] text={(byte)0x01,(byte)0x23,(byte)0x45,(byte)0x67,(byte)0x89,(byte)0xAB,(byte)0xCD,(byte)0xEF};
        System.out.println("---------------------------------DES-------------------------------");
        Des des=new Des(new FeistelNetwork(),new RoundKeysForDes());
        System.out.println("Text:");
        DebugFunctions.debugByteHexArray(text);
        System.out.println("key:");
        des.makeRoundKeys(key);
        DebugFunctions.debugByteHexArray(key);
        text=des.encrypt(text);
        System.out.println("Encrypt Text:");
        DebugFunctions.debugByteHexArray(text);
        text=des.decrypt(text);
        System.out.println("Decrypt Text:");
        DebugFunctions.debugByteHexArray(text);
        System.out.println("---------------------------------DES-------------------------------");

    }
    static void testRijndael(){
        byte[] key={(byte)0x54, (byte)0x68, (byte)0x61, (byte)0x74, (byte)0x73, (byte)0x20, (byte)0x6D, (byte)0x79,
                (byte)0x20, (byte)0x4B, (byte)0x75,(byte)0x6E, (byte)0x67, (byte)0x20, (byte)0x46, (byte)0x75,
                (byte)0x20, (byte)0x4B, (byte)0x75,(byte)0x6E, (byte)0x67, (byte)0x20, (byte)0x46, (byte)0x75};
        byte[] text={(byte)0x54, (byte)0x77,(byte)0x6F,(byte)0x20,(byte)0x4F,(byte)0x6E,(byte)0x65,(byte)0x20,
                (byte)0x4E,(byte)0x69,(byte)0x6E,(byte)0x65,(byte)0x20,(byte)0x54,(byte)0x77,(byte)0x6F,
                (byte)0x4E,(byte)0x69,(byte)0x6E,(byte)0x65,(byte)0x20,(byte)0x54,(byte)0x77,(byte)0x6F};
        System.out.println("---------------------------------Rijndel--------------------------");
        System.out.println("Text:");
        DebugFunctions.debugByteHexArray(text);
        System.out.println("key:");
        DebugFunctions.debugByteHexArray(key);
        Rijndael rijndael=new Rijndael(new RijndaelRound(),new RoundKeysForRijndael());
        rijndael.setGFModule((byte)0b0100_1101);
        rijndael.makeRoundKeys(key);
        text=rijndael.encrypt(text);
        System.out.println("Encrypt Text:");
        DebugFunctions.debugByteHexArray(text);
        System.out.println("Decrypt Text:");
        text=rijndael.decrypt(text);
        DebugFunctions.debugByteHexArray(text);
        System.out.println("---------------------------------Rijndel--------------------------");


    }
    static void testMode(Mode mode, BaseEncryption encryption, Object...objects){
        ExecutorService server= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        BaseModeEncryption.setService(server);
        byte[] key={(byte)0x54, (byte)0x68, (byte)0x61, (byte)0x74, (byte)0x73, (byte)0x20, (byte)0x6D, (byte)0x79,
                    (byte)0x20, (byte)0x4B, (byte)0x75,(byte)0x6E, (byte)0x67, (byte)0x20, (byte)0x46, (byte)0x75,
                    (byte)0x20, (byte)0x4B, (byte)0x75,(byte)0x6E, (byte)0x67, (byte)0x20, (byte)0x46, (byte)0x75};
        byte[] text={(byte)0x54, (byte)0x77,(byte)0x6F,(byte)0x20,(byte)0x4F,(byte)0x6E,(byte)0x65,(byte)0x20,
                     (byte)0x4E,(byte)0x69,(byte)0x6E,(byte)0x65,(byte)0x20,(byte)0x54,(byte)0x77,(byte)0x6F,
                      (byte)0x4E,(byte)0x69,(byte)0x6E,(byte)0x65,(byte)0x20,(byte)0x54,(byte)0x77,(byte)0x6F};
        BaseModeEncryption modeEncryption=new BaseModeEncryption(key,mode,new PaddingPKCS7(),encryption ,text,objects[0]);
        modeEncryption.waiting();
        modeEncryption.encrypt("./candle.blend","./decript.blend");
        modeEncryption.waiting();
        modeEncryption.decrypt("./decript.blend","./result.blend");
        modeEncryption.waiting();
        //Thread.sleep(1000);
        server.shutdown();
    }

    public static void main(String[] args) {
        Rijndael rijndael=new Rijndael(new RijndaelRound(),new RoundKeysForRijndael());
        rijndael.setGFModule((byte)0b0100_1101);
        Des des=new Des(new FeistelNetwork(),new RoundKeysForDes());
       testMode(Mode.CTR,rijndael,(int)5);
       // testRijndael();
    }
}