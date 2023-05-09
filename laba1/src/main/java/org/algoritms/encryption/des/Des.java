package org.algoritms.encryption.des;

import org.algoritms.encryption.BaseBlockEncryption;
import org.algoritms.encryption.BaseEncryption;
import org.algoritms.encryption.des.help.ConstantValues;
import org.algoritms.help.BitOperations;
import org.algoritms.encryption.BaseRoundKeysGeneration;
import java.util.List;

public class Des implements BaseEncryption {
    protected BaseBlockEncryption blockEncryption;
    private final BaseRoundKeysGeneration roundKeysGeneration;
    protected List<byte[]> roundKeys=null;
    private record Arrays(){
        private static int[] IPL=ConstantValues.makeIPL();
        private static int[] IPR=ConstantValues.makeIPR();
        private static int[] IP_1=ConstantValues.makeIP_1();

    }

    private static  final int COUNT_BLOCK=8;
    protected Integer countRound=16;

    public Des(BaseBlockEncryption blockEncryption, BaseRoundKeysGeneration roundKeysGeneration) {
        this.blockEncryption=blockEncryption;
        this.roundKeysGeneration=roundKeysGeneration;
    }
    private void isBlockCorrect(byte[]block)throws IllegalArgumentException{
        if(block.length!=COUNT_BLOCK){
            throw new IllegalArgumentException("block length not correct");
        }
        if(roundKeys==null){
            throw new IllegalArgumentException("not round keys");
        }
    }
    private byte[] makeT(byte[] r, byte[] l){
        byte[]t=new byte[r.length+l.length];
        for(int i=0; i<(t.length>>1); i++){
            t[i]=r[i];
            t[r.length+i]=l[i];
        }
        return t;
    }
    @Override
    public byte[] encrypt(byte[] block) throws IllegalArgumentException{
        isBlockCorrect(block);
        byte[] l= BitOperations.makePermutation(block, Arrays.IPL);
        byte[] r= BitOperations.makePermutation(block, Arrays.IPR);
        for(int currentRound=0; currentRound<countRound; currentRound++){
            byte[] currentR=r.clone();
            r= BitOperations.makeXor(l,blockEncryption.encryptTheBlock(r,this.roundKeys.get(currentRound)));
            l=currentR;
        }
        return BitOperations.makePermutation(makeT(r,l), Arrays.IP_1);
    }

    @Override
    public byte[] decrypt(byte[] block) throws IllegalArgumentException{
        isBlockCorrect(block);
        byte[] l= BitOperations.makePermutation(block, Arrays.IPL);
        byte[] r= BitOperations.makePermutation(block, Arrays.IPR);
        for(int currentRound=(countRound-1); currentRound>=0; currentRound--){
            byte[] currentR=r.clone();
            r= BitOperations.makeXor(l, blockEncryption.encryptTheBlock(r,this.roundKeys.get(currentRound)));
            l=currentR;
        }
        return BitOperations.makePermutation(makeT(r,l), Arrays.IP_1);
    }
    @Override
    public void makeRoundKeys(byte[] key)throws IllegalArgumentException{
        if(key.length!=7){
            throw new IllegalArgumentException("key length must be 8 bytes");
        }
        roundKeys=roundKeysGeneration.generateRoundKeys(key);
    }

    @Override
    public int getCountBlock() {
        return COUNT_BLOCK;
    }
}
