package Coursework.Twofish.Interfase;

public interface BaseEncryption {
    byte[] encrypt(byte[] block);

    byte[] decrypt(byte[] block);

    void makeRoundKeys(byte[] key);
    int getCountBlock();
}
