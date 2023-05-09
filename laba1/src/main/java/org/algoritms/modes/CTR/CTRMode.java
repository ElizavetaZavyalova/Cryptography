package org.algoritms.modes.CTR;

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

public class CTRMode extends BaseMode {
    static private final int CURRENT_BLOCK=0;

    public CTRMode(Settings settings) {
        super(settings);
    }
    byte[] makeNextCurrentBlock(byte[] currentBlock){
        return currentBlock;
    }

    @Override
    public void encrypt(BufferedInputStream input, FileOutputStream output) throws IOException, ExecutionException, InterruptedException {
        futures.clear();
        byte[]currentBlock= settings.getInitialBlock();
        output.write(settings.encryption.encrypt(currentBlock));
        byte [] bytes = new byte [settings.getCountByte()];
        while(!isFileEnd(input,bytes)){
            currentBlock=makeNextCurrentBlock(currentBlock);
            futures.add(settings.service.submit(new ModeRun(bytes,(Object) currentBlock.clone()) {
                @Override
                public byte[] call(){
                    byte[] currentBlock=settings.encryption.encrypt((byte[])objects[CURRENT_BLOCK]);
                    return BitOperations.makeXor(block,currentBlock);
                }
            }));
            bytes = new byte [settings.getCountByte()];
        }
        for(Future<byte[]> result:futures) {
            output.write(result.get());
        }
    }

    @Override
    public void decrypt(FileInputStream input, FileOutputStream output) throws IOException, ExecutionException, InterruptedException {
        futures.clear();
        byte[]bytes=new byte[settings.getCountByte()];
        byte[]currentBlock=new byte[settings.encryption.getCountBlock()];
        input.read(currentBlock);
        currentBlock=settings.encryption.decrypt(currentBlock);
        while(input.read(bytes)!=-1){
            currentBlock=makeNextCurrentBlock(currentBlock);
            futures.add(settings.service.submit(new ModeRun(bytes,(Object) currentBlock.clone()) {
                @Override
                public byte[] call(){
                    byte[] currentBlock=settings.encryption.encrypt((byte[])objects[CURRENT_BLOCK]);
                    return BitOperations.makeXor(block,currentBlock);
                }
            }));
            bytes=new byte[settings.getCountByte()];
        }
        writingAll(output);
    }
}
