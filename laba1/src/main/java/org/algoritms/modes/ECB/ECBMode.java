package org.algoritms.modes.ECB;
import org.algoritms.modes.Settings.Settings;
import org.algoritms.modes.BaseMode;

import java.io.*;
import java.util.ArrayList;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ECBMode extends BaseMode {
    public ECBMode(Settings settings) {
        super(settings);
    }

    @Override
    public void encrypt(BufferedInputStream input, FileOutputStream output) throws IOException, ExecutionException, InterruptedException {//+
        futures.clear();
        byte [] bytes = new byte [settings.encryption.getCountBlock()];
        while(!isFileEnd(input,bytes)){
            futures.add(settings.service.submit(new ModeRun(bytes) {
                @Override
                public byte[] call(){
                    return settings.encryption.encrypt(block);
                }
            }));
            bytes = new byte [settings.encryption.getCountBlock()];
        }
        for(Future<byte[]> result:futures) {
            output.write(result.get());
       }
    }

    @Override
    public void decrypt(FileInputStream input, FileOutputStream output) throws IOException, ExecutionException, InterruptedException {//+
        futures.clear();
        byte [] bytes = new byte [settings.encryption.getCountBlock()];
        while(input.read(bytes)!=-1){
            futures.add(settings.service.submit(new ModeRun(bytes) {
                @Override
                public byte[] call(){
                    return settings.encryption.decrypt(block);
                }
            }));
            bytes = new byte [settings.encryption.getCountBlock()];
        }
        writingAll(output);
    }
}
