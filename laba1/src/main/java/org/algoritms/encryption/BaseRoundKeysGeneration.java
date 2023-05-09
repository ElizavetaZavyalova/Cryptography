package org.algoritms.encryption;

import java.util.ArrayList;

public interface BaseRoundKeysGeneration{
    ArrayList<byte[]> generateRoundKeys(byte[] roundKey);
}
