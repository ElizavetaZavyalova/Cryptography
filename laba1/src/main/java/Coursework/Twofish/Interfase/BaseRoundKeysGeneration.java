package Coursework.Twofish.Interfase;

import java.util.ArrayList;

public interface BaseRoundKeysGeneration<T>{
    ArrayList<T> generateRoundKeys(byte[] roundKey);
}
