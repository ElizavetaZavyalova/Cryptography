package org.algoritms.encryption.rijndel;

import org.algoritms.encryption.BaseBlockEncryption;
import org.algoritms.encryption.BaseEncryption;
import org.algoritms.encryption.rijndel.help.GF;
import org.algoritms.encryption.rijndel.help.Matrix;
import org.algoritms.encryption.rijndel.help.ReplaceForRijndael;
import org.algoritms.encryption.rijndel.help.RijndaelSettings;
import org.algoritms.help.*;
import org.algoritms.encryption.BaseRoundKeysGeneration;

import java.util.List;

public class Rijndael implements BaseEncryption {
    protected BaseBlockEncryption blockEncryption;
    protected List<byte[]> roundKeys;
    private final BaseRoundKeysGeneration roundKeysGeneration;
    RijndaelSettings settings;
    int blockSize=0;
    private record Encrypt(){
        static byte[] subBytes(byte[]block,byte[][]sBox){
            return ReplaceForRijndael.replace(block,sBox);
        }
        static  byte[] shiftRows(byte[]block,int shiftDirection){
            Matrix blockMatrix=new Matrix(block);
            Matrix result=new Matrix(block.length);
            for(int stringIndex=0; stringIndex<blockMatrix.getStringCount();stringIndex++){
                byte[]string=blockMatrix.makeString(stringIndex);
                string= BitOperations.makeCircularShift(string, shiftDirection*stringIndex);
                result.setString(string,stringIndex);
            }
            return result.getMatrix();
        }
        static byte[]mixColumns(byte[]block,byte[]mixVector,byte module){
            Matrix blockMatrix=new Matrix(block);
            Matrix result=new Matrix(block.length);
            for(int columnIndex=0; columnIndex<blockMatrix.getColumnCount();columnIndex++){
                byte[]column=makeMultiplicationsOfVectors(blockMatrix.makeColumn(columnIndex),mixVector,module);
                result.setColumn(column,columnIndex);
            }
            return result.getMatrix();
        }
        static byte[]makeMultiplicationsOfVectors(byte[]column,byte[]mixVector,byte module) {
            byte[]result=new byte[column.length];
            for (int elem = 0; elem < column.length;elem++) {
                result[elem] =0;
                byte[]currentColumn= BitOperations.makeCircularShift(mixVector,-elem);
                for (int index = 0; index < column.length; index++) {
                    result[elem] = GF.sum(GF.multiplication(currentColumn[index],column[index],module),result[elem]);
                }
            }
            return result;
        }
    }
    public Rijndael(BaseBlockEncryption blockEncryption, BaseRoundKeysGeneration roundKeysGeneration)throws IllegalArgumentException {
        if(!(roundKeysGeneration instanceof RoundKeysForRijndael)){
            throw new IllegalArgumentException("must be RoundKeysForRijndael");
        }
        this.settings=new RijndaelSettings((byte)0b0001_1011);
        ((RoundKeysForRijndael)roundKeysGeneration).setSettings(this.settings);
        this.blockEncryption = blockEncryption;
        this.roundKeysGeneration = roundKeysGeneration;
    }
    @Override
    public void makeRoundKeys(byte[] key) throws IllegalArgumentException {
        if(key.length*8%32!=0||key.length*8<32){
            throw new IllegalArgumentException("key must be key%32==0");
        }
        blockSize=key.length;
        roundKeys=roundKeysGeneration.generateRoundKeys(key);
    }
    @Override
    public byte[] encrypt(byte[] block) throws IllegalArgumentException{
        isBlockCorrect(block);
        byte[]result=block;
        result=this.blockEncryption.encryptTheBlock(result,roundKeys.get(0));
        for(int round=1; round<(roundKeys.size()-1);round++){
            result=Encrypt.subBytes(result,settings.sBoxEncrypt);
            result=Encrypt.shiftRows(result,1);
            result=Encrypt.mixColumns(result,settings.mixMatrixEncrypt, settings.module);
            result=this.blockEncryption.encryptTheBlock(result,roundKeys.get(round));
        }
        result=Encrypt.subBytes(result,settings.sBoxEncrypt);
        result=Encrypt.shiftRows(result,1);
        result=this.blockEncryption.encryptTheBlock(result,roundKeys.get(roundKeys.size()-1));
        return result;
    }
    void isBlockCorrect(byte[] block)throws IllegalArgumentException{
        if(blockSize<=0){
            throw new IllegalArgumentException("not round keys");
        }
        if(block.length!=blockSize){
            throw new IllegalArgumentException("block length not correct");
        }
    }
    @Override
    public byte[] decrypt(byte[] block) throws IllegalArgumentException{
       isBlockCorrect(block);
        byte[]result=block;
        result=this.blockEncryption.encryptTheBlock(result,roundKeys.get(roundKeys.size()-1));
        for(int round=(roundKeys.size()-2);round>0; round--){
            result=Encrypt.shiftRows(result,-1);
            result=Encrypt.subBytes(result, settings.sBoxDecrypt);
            result=this.blockEncryption.encryptTheBlock(result,roundKeys.get(round));
            result=Encrypt.mixColumns(result,settings.mixMatrixDecrypt, settings.module);
        }
        result=Encrypt.shiftRows(result,-1);
        result=Encrypt.subBytes(result, settings.sBoxDecrypt);
        result=this.blockEncryption.encryptTheBlock(result,roundKeys.get(0));
        return result;
    }
    public void setGFModule(byte module)throws IllegalArgumentException{
        settings.setModule(module);
    }
    @Override
    public int getCountBlock() {
        return  blockSize;
    }
}
