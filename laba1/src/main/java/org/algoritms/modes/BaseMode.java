package org.algoritms.modes;
import org.algoritms.modes.Settings.Settings;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class BaseMode {
    protected Settings settings=null;
    private boolean eddPadding=false;
    protected List<Future<byte[]>> futures=new ArrayList<>();

    protected BaseMode(Settings settings) {
        this.settings=settings;

    }
    protected boolean isFileEnd(BufferedInputStream input,byte[]bytes) throws IOException {
        if(bytes.length<=1){
            return input.read(bytes)==-1;
        }
        if(input.available()<bytes.length){
            if(eddPadding){
                input.read(bytes);
                settings.eddPadding(bytes);
                eddPadding=false;
                return false;
            }
            return true;
        }
        if(input.available()<2*bytes.length){
            eddPadding=true;
            input.read(bytes);
            return false;
        }
        input.read(bytes);
        return false;
    }
    protected  void writingAll(FileOutputStream output) throws ExecutionException, InterruptedException, IOException {
        for(int rezult=0; rezult<futures.size()-1; rezult++) {
            output.write(futures.get(rezult).get());
        }
        byte[]block=settings.deletePadding(futures.get(futures.size()-1).get());
        output.write(block);
    }
    public abstract void encrypt(BufferedInputStream input, FileOutputStream output) throws IOException, ExecutionException, InterruptedException;
    public abstract void decrypt(FileInputStream input, FileOutputStream output) throws IOException, ExecutionException, InterruptedException;
    public abstract static class ModeRun implements Callable<byte[]> {
        protected byte[] block;
        protected Object[]objects;


        protected ModeRun(byte[] block,Object...objects){
            this.block=block;
            this.objects=objects;
        }
    }
}
