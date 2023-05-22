package Coursework.Twofish.Interfase;

public interface BaseBlockEncryption {
    byte[] encryptTheBlock(byte[] block,  byte[] roundKey);
}
