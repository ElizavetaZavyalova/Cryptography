package org.algoritms.encryption.rijndel;

import lombok.Setter;
import org.algoritms.encryption.rijndel.help.GF;
import org.algoritms.encryption.rijndel.help.ReplaceForRijndael;
import org.algoritms.help.BitOperations;
import org.algoritms.encryption.rijndel.help.Matrix;
import org.algoritms.encryption.rijndel.help.RijndaelSettings;
import org.algoritms.encryption.BaseRoundKeysGeneration;

import java.util.ArrayList;

public class RoundKeysForRijndael implements BaseRoundKeysGeneration {
    @Setter
    RijndaelSettings settings;
    @Override
    public ArrayList<byte[]> generateRoundKeys(byte[] roundKey) {
        int nk=roundKey.length/4;
        ArrayList<byte[]> keys=new ArrayList<>();
        keys.add(roundKey);
        Matrix roundKeyMatrix=new Matrix(roundKey);
        for(int numberOfRound=0; numberOfRound<(nk+6); numberOfRound++){
            roundKeyMatrix=makeCurrentKey(roundKeyMatrix,numberOfRound);
            keys.add(roundKeyMatrix.getMatrix());
        }
        return keys;
    }
    Matrix makeCurrentKey(Matrix roundKeyMatrix,int numberOfRound){
        Matrix currentKey=new Matrix(roundKeyMatrix.getLength());
        byte[] column=makeFirstColumn(numberOfRound,roundKeyMatrix);
        currentKey.setColumn(column,0);
        for(int columnIndex=1;columnIndex<currentKey.getColumnCount();columnIndex++){
            column= BitOperations.makeXor(roundKeyMatrix.makeColumn(columnIndex),column);
            currentKey.setColumn(column,columnIndex);
        }
        return currentKey;
    }
    byte[]makeFirstColumn(int numberOfRound, Matrix roundKeyMatrix){
        byte[] rCON={makeFirstRCONForCurrentRound(numberOfRound),0,0,0};
        byte[] column=roundKeyMatrix.makeColumn(roundKeyMatrix.getColumnCount()-1);
        column= BitOperations.makeCircularShift(column, 1);
        column= ReplaceForRijndael.replace(column, settings.sBoxEncrypt);
        return BitOperations.makeXor(BitOperations.makeXor(column,rCON),roundKeyMatrix.makeColumn(0));
    }
    protected byte makeFirstRCONForCurrentRound(int round){
        return GF.mod(1<<round,settings.module);
    }
}
