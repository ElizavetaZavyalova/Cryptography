package org.algoritms.encryption.rijndel.help;

public class Matrix{
    private int stringCount=4;
    private int columnCount;
     byte[] matrixArray;
    public byte[] getMatrix() {
        return matrixArray;
    }
    public int getColumnCount() {
        return columnCount;
    }
    public int getLength(){
        return matrixArray.length;
    }
    public int getStringCount() {
        return stringCount;
    }
    public Matrix(byte[] matrix){
        this.columnCount=matrix.length/4;
        this.matrixArray=matrix;
    }
    public Matrix(int length){
        this.columnCount=length/4;
        this.matrixArray=new byte[length];
    }
    public int index(int stringIndex, int columnIndex){
        return columnIndex*stringCount+stringIndex;
    }
    public byte[] makeColumn(int columnIndex){
        byte[]result=new byte[stringCount];
        for(int stringIndex=0; stringIndex<stringCount; stringIndex++){
            result[stringIndex]=matrixArray[index(stringIndex,columnIndex)];
        }
        return result;
    }
    public byte[] makeString(int stringIndex){
        byte[]result=new byte[columnCount];
        for(int columnIndex=0; columnIndex<columnCount; columnIndex++){
            result[columnIndex]=matrixArray[index(stringIndex,columnIndex)];
        }
        return result;
    }
    public void setColumn(byte[] column,int columnIndex){
        for(int stringIndex=0; stringIndex<stringCount;stringIndex++){
            matrixArray[index(stringIndex,columnIndex)]=column[stringIndex];
        }
    }
    public void setString(byte[] string,int stringIndex){
        for(int columnIndex=0; columnIndex<columnCount;columnIndex++){
            matrixArray[index(stringIndex,columnIndex)]=string[columnIndex];
        }
    }
}
