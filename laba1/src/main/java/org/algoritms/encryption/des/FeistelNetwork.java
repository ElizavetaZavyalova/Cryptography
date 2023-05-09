package org.algoritms.encryption.des;

import org.algoritms.encryption.BaseBlockEncryption;
import org.algoritms.encryption.des.help.ConstantValues;
import org.algoritms.encryption.des.help.ReplaceForDes;
import org.algoritms.help.BitOperations;

public class FeistelNetwork implements BaseBlockEncryption {
    private final int[] maskE = ConstantValues.makeE();
    private final int[] maskP = ConstantValues.makeP();
    private final byte[][][] maskS = ConstantValues.makeS();
    @Override
    public byte[] encryptTheBlock(byte[] block, byte[] roundKey) {
        byte[] newBlock = BitOperations.makePermutation(block, maskE);
        newBlock = BitOperations.makeXor(newBlock, roundKey);
        newBlock = ReplaceForDes.replace(newBlock, maskS);
        newBlock = BitOperations.makePermutation(newBlock, maskP);
        return newBlock;
    }

}





















