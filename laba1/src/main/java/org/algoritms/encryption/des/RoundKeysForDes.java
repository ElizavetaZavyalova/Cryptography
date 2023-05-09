package org.algoritms.encryption.des;

import org.algoritms.encryption.des.help.ConstantValues;
import org.algoritms.help.BitOperations;
import org.algoritms.encryption.BaseRoundKeysGeneration;

import java.util.ArrayList;

public class RoundKeysForDes implements BaseRoundKeysGeneration {
    @Override
    public ArrayList<byte[]> generateRoundKeys(byte[] roundKey) {
        ArrayList<byte[]> keys=new ArrayList<>();
        byte[]keyWithControlBits=makeKeyWithControlBits(roundKey);
        byte[]cd= BitOperations.makePermutation(keyWithControlBits, ConstantValues.makeC0D0());
        int[] permutationRule=ConstantValues.makeK();
        for(int shift:ConstantValues.makeShift()){
            circularShift(cd,shift);
            byte[] key= BitOperations.makePermutation(cd,permutationRule);
            keys.add(key);
        }
        return keys;
    }
    private static void circularShift(byte[] cd, int shiftCount){
        byte byteCentralR=(byte)(cd[3]&0x0F);
        byte byteCentralL=(byte)(cd[3]&0xF0);
        byte first=(byte)(((cd[0]&0xFF)>>(8-shiftCount))<<4);
        cd[0]=(byte)((cd[0]&0xFF)<<(shiftCount));
        for(int i=1; i<cd.length;i++){
            cd[i-1]=(byte)((cd[i-1]&0xFF)|(cd[i]&0xFF)>>(8-shiftCount));
            cd[i]=(byte)((cd[i]&0xFF)<<(shiftCount));
            if(i==3){
                cd[3]=(byte)((byteCentralL<<shiftCount)|(first|byteCentralR));
                byteCentralR=(byte)(cd[3]&0x0F);
                byteCentralL=(byte)(cd[3]&0xF0);
                cd[3]=(byte)((cd[3]&0xFF)<<(shiftCount));
            }
        }
        cd[cd.length-1]=(byte)((cd[cd.length-1]&0xFF)|(byteCentralR&0x0F)>>(4-shiftCount));
        cd[3]=(byte)((cd[3]&0x0F)| byteCentralL);
    }
    public byte[] makeKeyWithControlBits(byte[] roundKey){
        byte[] keyWithControlBits=new byte[8];
        keyWithControlBits[0]=(byte)(roundKey[0]&0xFE);
        keyWithControlBits[0]=(byte)(keyWithControlBits[0]|(BitOperations.makeByteXor(keyWithControlBits[0])^1));
        for(int i=1; i<7; i++){
            keyWithControlBits[i]=(byte)((((roundKey[i-1]&0xFF)<<(8-i))|((roundKey[i]&0xFF)>>i))&0xFE);
            keyWithControlBits[i]=(byte)(keyWithControlBits[i]|(BitOperations.makeByteXor(keyWithControlBits[i])^1));

        }
        keyWithControlBits[7]=(byte)(((roundKey[6]&0xFF)<<1)&0xFE);
        keyWithControlBits[7]=(byte)(keyWithControlBits[7]|(BitOperations.makeByteXor(keyWithControlBits[7])^1));
        return keyWithControlBits;
    }

}
