package org.algoritms.encryption;

public interface BaseBlockEncryption {
    byte[] encryptTheBlock(byte[] block,  byte[] roundKey);
}
