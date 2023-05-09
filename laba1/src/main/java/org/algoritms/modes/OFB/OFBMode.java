package org.algoritms.modes.OFB;


import org.algoritms.help.BitOperations;
import org.algoritms.modes.BaseMode;
import org.algoritms.modes.Settings.Settings;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class OFBMode extends BaseMode {

    public OFBMode(Settings settings) {
        super(settings);
    }
    @Override
    public void encrypt(BufferedInputStream input, FileOutputStream output) throws IOException {//-
        byte[]bytes=new byte[settings.getCountByte()];
        byte[]currentBlock=settings.getInitialBlock();
        output.write(settings.encryption.encrypt(currentBlock));
        while(!isFileEnd(input,bytes)) {
            currentBlock=settings.encryption.encrypt(currentBlock);
            bytes= BitOperations.makeXor(bytes,currentBlock);
            output.write(bytes);
            bytes=new byte[settings.getCountByte()];
        }
    }
    @Override
    public void decrypt(FileInputStream input, FileOutputStream output) throws IOException {//-
        byte[]bytes=new byte[settings.getCountByte()];
        byte[]currentBlock=new byte[settings.encryption.getCountBlock()];
        input.read(currentBlock);
        currentBlock=settings.encryption.decrypt(currentBlock);
        while(input.read(bytes)!=-1) {
            currentBlock=settings.encryption.encrypt(currentBlock);
            bytes= BitOperations.makeXor(bytes,currentBlock);
            if(input.available()<=0) {
                bytes=settings.deletePadding(bytes);
            }
            output.write(bytes);
            bytes=new byte[settings.getCountByte()];
        }
    }
}
