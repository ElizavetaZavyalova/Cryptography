package org.algoritms.modes.CFB;

import org.algoritms.help.BitOperations;
import org.algoritms.modes.Settings.Settings;
import org.algoritms.modes.BaseMode;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CFBMode extends BaseMode {

    private static final int CURRENT_BLOCK=0;
    public CFBMode(Settings settings) {
        super(settings);

    }
    void makeShift(byte[] block){
        for(int i=0;i<block.length-this.settings.getCountByte();i++){
            block[i]=block[i+this.settings.getCountByte()];
        }
    }
    void eddBlock(byte[]block,byte[]eddBlock){
        int firstEmptyBlock=block.length-eddBlock.length;
        for(int i=0;i<eddBlock.length;i++){
            block[i+firstEmptyBlock]=eddBlock[i];
        }
    }
    @Override
    public void encrypt(BufferedInputStream input, FileOutputStream output) throws IOException {//-
        byte[]bytes=new byte[this.settings.getCountByte()];
        byte[]currentBlock=settings.getInitialBlock();
        output.write(settings.encryption.encrypt(currentBlock));
        while(!isFileEnd(input,bytes)){
            byte[]lastBlock=settings.encryption.encrypt(currentBlock);
            bytes= BitOperations.makeXor(bytes,lastBlock);//bytes.length
            output.write(bytes);
            makeShift(currentBlock);
            eddBlock(currentBlock,bytes);
            bytes=new byte[settings.getCountByte()];
        }
    }

    @Override
    public void decrypt(FileInputStream input, FileOutputStream output) throws IOException, ExecutionException, InterruptedException {//+
        futures.clear();
        byte[]bytes=new byte[settings.getCountByte()];
        byte[]currentBlock=new byte[settings.encryption.getCountBlock()];
        input.read(currentBlock);
        currentBlock=settings.encryption.decrypt(currentBlock);
        while(input.read(bytes)!=-1){
             futures.add(settings.service.submit(new ModeRun(bytes.clone(), (Object) currentBlock.clone()) {
                     @Override
                     public byte[] call() {
                         assert(objects[CURRENT_BLOCK] instanceof byte[]);
                         byte[] lastBlock = settings.encryption.encrypt((byte[]) objects[CURRENT_BLOCK]);
                         block = BitOperations.makeXor(block, lastBlock);//bytes.length
                         return block;
                     }
             }));
             makeShift(currentBlock);
             eddBlock(currentBlock,bytes);
             bytes=new byte[settings.getCountByte()];
        }
        writingAll(output);
    }
}
