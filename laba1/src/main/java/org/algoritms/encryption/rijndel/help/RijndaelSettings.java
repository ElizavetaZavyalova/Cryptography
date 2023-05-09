package org.algoritms.encryption.rijndel.help;

import org.algoritms.help.BitOperations;

public class RijndaelSettings {
    public final byte [][] sBoxEncrypt;
    public byte[]mixMatrixEncrypt;
    public final byte [][] sBoxDecrypt;
    public byte[]mixMatrixDecrypt;
    public byte module;
    private void generateSBox(byte polynomial){
            int sBoxSize=16;
            byte elem=0;
            for(int stringIndex=0;  stringIndex<sBoxSize; stringIndex++) {
                for (int columnIndex = 0; columnIndex < sBoxSize; columnIndex++) {
                    byte currentElem = generateSBoxElem(polynomial, elem);
                    sBoxEncrypt[stringIndex][columnIndex] = currentElem;
                    int currentString = ((currentElem & 0xF0) >> 4);
                    int currentColumn = currentElem & 0x0F;
                    sBoxDecrypt[currentString][currentColumn] = (byte) ((stringIndex << 4) | columnIndex);
                    elem++;
                }
            }
    }
    private static byte generateSBoxElem(byte polynomial, byte currentElem){
            int initialConstant=0xf8;
            int resultConstant= 0x63;
            int result = 0;
            byte inV = GF.getReverse(currentElem,polynomial);
            for (int stringIndex = 0; stringIndex < 8; stringIndex++) {
                byte currentString = (byte) ((((initialConstant) >> stringIndex) | ((initialConstant) << 8-stringIndex))&0xFF);
                result =( result << 1) | BitOperations.makeByteXor((byte) (inV & currentString));
            }
            return (byte) (result^resultConstant);
    }
    private void generateMixMatrix(){
        mixMatrixEncrypt=new byte[]{(byte)0x02,(byte)0x03,(byte)0x01,(byte)0x01};
        mixMatrixDecrypt=new byte[]{(byte)0x0e,(byte)0x0b,(byte)0x0d,(byte)0x09};
    }
    public RijndaelSettings(byte module)throws IllegalArgumentException{
        sBoxEncrypt=new byte[16][16];
        sBoxDecrypt=new byte[16][16];
        setModule(module);
        generateMixMatrix();
    }
    public void setModule(byte module)throws IllegalArgumentException{
        if(!GF.isSimple(module)){
            throw new IllegalArgumentException(module+"Must be simple");
        }
        this.module=module;
        generateSBox(module);
    }
}
