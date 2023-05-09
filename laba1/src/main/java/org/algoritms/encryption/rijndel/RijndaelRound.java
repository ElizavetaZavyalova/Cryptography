package org.algoritms.encryption.rijndel;
import org.algoritms.encryption.BaseBlockEncryption;
import org.algoritms.help.BitOperations;
import org.algoritms.encryption.rijndel.help.Matrix;

public class RijndaelRound implements BaseBlockEncryption {
    @Override
    public byte[] encryptTheBlock(byte[] block, byte[] roundKey) {
        Matrix result=new Matrix(block.length);
        Matrix blockMatrix=new Matrix(block);
        Matrix roundKeyMatrix=new Matrix(roundKey);
        for(int columnIndex=0;columnIndex<result.getColumnCount();columnIndex++){
            byte[]column= BitOperations.makeXor(blockMatrix.makeColumn(columnIndex),roundKeyMatrix.makeColumn(columnIndex));
            result.setColumn(column,columnIndex);
        }
        return result.getMatrix();
    }
}
