package org.algoritms.encryption;

import java.util.ArrayList;

public interface BaseRoundKeysGeneration<T>{
    ArrayList<T> generateRoundKeys(byte[] roundKey);
}
