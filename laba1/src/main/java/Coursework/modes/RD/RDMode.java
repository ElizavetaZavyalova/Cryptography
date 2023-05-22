package Coursework.modes.RD;

import Coursework.modes.Settings.BitOperations;
import Coursework.modes.BaseMode;
import Coursework.modes.Settings.Settings;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class RDMode extends BaseMode {
    private static final int CURRENT_BLOCK=0;
    boolean hash=false;
    public RDMode(Settings settings,boolean hash) {
        super(settings);
        this.hash=hash;
    }
    byte[] makeNextCurrentBlock(byte[] currentBlock){
        return currentBlock;
    }
    void eddHash(){
        if(hash) {
            int count = 0;
            while (count != settings.getCountByte()) {
                futures.add(settings.service.submit(new ModeRun(settings.makeRandomBlock()) {
                    @Override
                    public byte[] call() {
                        return settings.encryption.encrypt(block);
                    }
                }));
                count++;
            }
        }
    }
    @Override
    public void encrypt(BufferedInputStream input, FileOutputStream output) throws IOException, ExecutionException, InterruptedException {
        futures.clear();
        byte[]currentBlock= settings.getInitialBlock();
        output.write(settings.encryption.encrypt(currentBlock));
        byte [] bytes = new byte [settings.encryption.getCountBlock()];
        eddHash();
        while(!isFileEnd(input,bytes)){
            futures.add(settings.service.submit(new ModeRun(bytes,(Object) currentBlock.clone()) {
                @Override
                public byte[] call(){
                    block=BitOperations.makeXor(block,(byte[])objects[CURRENT_BLOCK]);
                    return settings.encryption.encrypt(block);
                }

            }));
            currentBlock=makeNextCurrentBlock(currentBlock);
            bytes = new byte [settings.encryption.getCountBlock()];
        }
        for(Future<byte[]> result:futures) {
            output.write(result.get());
        }
    }
    private void missHash(FileInputStream input) throws IOException {
        if(hash){
            input.readNBytes(settings.encryption.getCountBlock()*settings.getCountByte());
        }
    }
    @Override
    public void decrypt(FileInputStream input, FileOutputStream output) throws IOException, ExecutionException, InterruptedException {
        futures.clear();
        byte[]bytes=new byte[settings.encryption.getCountBlock()];
        byte[]currentBlock=new byte[settings.encryption.getCountBlock()];
        input.read(currentBlock);
        currentBlock=settings.encryption.decrypt(currentBlock);
        missHash(input);
        while(input.read(bytes)!=-1){
            currentBlock=makeNextCurrentBlock(currentBlock);
            futures.add(settings.service.submit(new ModeRun(bytes,(Object) currentBlock.clone()) {
                @Override
                public byte[] call(){
                    block=settings.encryption.decrypt(block);
                    return BitOperations.makeXor(block,(byte[])objects[CURRENT_BLOCK]);
                }
            }));
            bytes=new byte[settings.encryption.getCountBlock()];
        }
        writingAll(output);
    }
}
