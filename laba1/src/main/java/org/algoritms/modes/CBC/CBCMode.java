package org.algoritms.modes.CBC;

import org.algoritms.help.BitOperations;
import org.algoritms.modes.BaseMode;
import org.algoritms.modes.Settings.Settings;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CBCMode extends BaseMode {
    private static final  int CURRENT_BLOCK=0;
    public CBCMode(Settings settings) {
        super(settings);

    }
    @Override
    public void encrypt(BufferedInputStream input, FileOutputStream output) throws IOException {//-
           byte[]bytes=new byte[settings.encryption.getCountBlock()];
           byte[]currentBlock= settings.getInitialBlock();
           output.write(settings.encryption.encrypt(currentBlock));
           while(!isFileEnd(input,bytes)){
               bytes= BitOperations.makeXor(bytes,currentBlock);
               bytes=settings.encryption.encrypt(bytes);
               output.write(bytes);
               currentBlock=bytes;
               bytes=new byte[settings.encryption.getCountBlock()];
           }
    }
    @Override
    public void decrypt(FileInputStream input, FileOutputStream output) throws IOException, ExecutionException, InterruptedException {//+
        futures.clear();
        byte[]bytes=new byte[settings.encryption.getCountBlock()];
        byte[]currentBlock=new byte[settings.encryption.getCountBlock()];
        input.read(currentBlock);
        currentBlock=settings.encryption.decrypt(currentBlock);
        while(input.read(bytes)!=-1){
            futures.add(settings.service.submit(new ModeRun(bytes.clone(), (Object) currentBlock) {
                @Override
                public byte[] call(){
                    assert(objects[CURRENT_BLOCK] instanceof byte[]);
                    block=settings.encryption.decrypt(block);
                    block= BitOperations.makeXor(block,(byte[])objects[CURRENT_BLOCK]);
                    return block;
                }
                }));
            currentBlock=bytes;
            bytes = new byte [settings.encryption.getCountBlock()];
        }
        writingAll(output);
    }
}
