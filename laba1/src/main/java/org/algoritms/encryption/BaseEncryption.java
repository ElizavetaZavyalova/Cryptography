package org.algoritms.encryption;

public interface BaseEncryption {
    byte[] encrypt(byte[] block);

    byte[] decrypt(byte[] block);

    void makeRoundKeys(byte[] key);
    int getCountBlock();
}
