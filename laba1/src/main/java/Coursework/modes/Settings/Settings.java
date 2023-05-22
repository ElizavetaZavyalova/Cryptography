package Coursework.modes.Settings;
import Coursework.Twofish.Interfase.BaseEncryption;
import lombok.Setter;
import Coursework.modes.Settings.padding.Padding;
import java.util.Random;
import java.util.concurrent.ExecutorService;

public class Settings {
    Random rnd=new Random();
    public ExecutorService service;
    public BaseEncryption encryption=null;
    @Setter
    protected Padding padding;
    @Setter
    protected byte[] initialBlock;
    @Setter
    protected int countByte=1;
    public void eddPadding(byte[]block) {
        if (block.length > 1) {
            padding.eddPadding(block);
        }
    }
    public byte[] deletePadding(byte[]block){
        if(block.length>1) {
          return  padding.deletePadding(block);
        }
        return block;
    }

    public Settings(ExecutorService service,Padding padding,BaseEncryption encryption) throws IllegalArgumentException{
        this.service = service;
        this.setPadding(padding);
        this.setEncryption(encryption);
    }
    public void setKey(byte[] key)throws IllegalArgumentException{
        encryption.makeRoundKeys(key);
    }
    public void setEncryption(BaseEncryption encryption){
        this.encryption=encryption;
    }

    public int getCountByte() {
        if(countByte>encryption.getCountBlock()){
            this.countByte=1;
        }
        return countByte;
    }
    public byte[] makeRandomBlock(){
        byte[] block=new byte[encryption.getCountBlock()];
        for (int b=0; b<initialBlock.length;b++){
            block[b]=(byte)(rnd.nextInt(256));
        }
        return block;
    }
    public byte[] getInitialBlock(){
        if(initialBlock==null||initialBlock.length!=encryption.getCountBlock()){
            this.initialBlock= makeRandomBlock();
        }
        return initialBlock;
    }
}
