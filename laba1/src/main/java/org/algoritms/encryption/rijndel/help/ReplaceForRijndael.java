package org.algoritms.encryption.rijndel.help;

import org.algoritms.help.BitOperations;

public record ReplaceForRijndael(){
    public static byte[] replace(byte[] block, byte[][]s){
        byte[]result=new byte[block.length];
        for(int i=0;i<block.length;i++) {
            result[i] = BitOperations.makeReplace(block[i],s,8);
        }
        return result;
    }
}
