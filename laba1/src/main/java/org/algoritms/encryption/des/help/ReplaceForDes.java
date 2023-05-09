package org.algoritms.encryption.des.help;

import org.algoritms.help.BitOperations;

public record ReplaceForDes() {
    enum Port {
        LEFT, RIGHT
    }

    public static byte[] replace(byte[] block, byte[][][] s) {
        byte[] newBlock = new byte[block.length - 2];
        int currentByte = 0;
        int currentS = 0;
        Port port = Port.LEFT;
        for (int i = 0; i < block.length * 8; i += 6) {
            byte currentBlock = 0;
            currentBlock = (byte) ((BitOperations.getBitFromByteArray(i, block) << 1) |
                    (BitOperations.getBitFromByteArray(i + 5, block) & 0xFF));
            for (int j = 1; j <= 4; j++) {
                currentBlock = (byte) ((currentBlock << 1) | (BitOperations.getBitFromByteArray(i + j, block) & 0xFF));
            }
            if (port == Port.LEFT) {
                port = Port.RIGHT;
                newBlock[currentByte] = (byte) (((BitOperations.makeReplace(currentBlock, s[currentS], 6))) << 4);
            } else {
                port = Port.LEFT;
                newBlock[currentByte] = (byte) (newBlock[currentByte] | (BitOperations.makeReplace(currentBlock, s[currentS], 6)));
                currentByte++;
            }
            currentS++;
        }
        return newBlock;
    }
}
