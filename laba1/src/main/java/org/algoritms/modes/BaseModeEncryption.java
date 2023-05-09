package org.algoritms.modes;

import org.algoritms.encryption.BaseEncryption;
import org.algoritms.modes.CFB.CFBMode;
import org.algoritms.modes.CTR.CTRMode;
import org.algoritms.modes.ECB.ECBMode;
import org.algoritms.modes.RD.RDMode;
import org.algoritms.modes.Settings.Settings;
import org.algoritms.modes.Settings.padding.Padding;
import org.algoritms.modes.CBC.CBCMode;
import org.algoritms.modes.OFB.OFBMode;
import org.algoritms.modes.enums.Mode;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;


public class BaseModeEncryption {
    protected static ExecutorService service = null;
    public Future<?> future = null;
    BaseMode encryptionMode = null;
    Map<Mode, BaseMode> encryptionModes = new HashMap<>();
    Settings settings;

    public BaseModeEncryption(byte[] key, Mode mode, Padding padding,BaseEncryption encryption,Object... objects) throws IllegalArgumentException {
        this.settings = new Settings(service,padding,encryption);
        encryptionModes.put(Mode.ECB, new ECBMode(settings));
        encryptionModes.put(Mode.CBC, new CBCMode(settings));
        encryptionModes.put(Mode.CFB, new CFBMode(settings));
        encryptionModes.put(Mode.OFB, new OFBMode(settings));
        encryptionModes.put(Mode.CTR, new CTRMode(settings));
        encryptionModes.put(Mode.RD,  new RDMode(settings,false));
        encryptionModes.put(Mode.RDH, new RDMode(settings,true));
        setEncryptionMode(mode,objects);
        setKey(key);
    }
    public void waiting(){
        assert (service != null);
        if (future != null) {
            while (!future.isDone()) {
                System.out.println("waiting");
            }
        }

    }

    public void encrypt(String inFile, String outFile) {
        future = service.submit(() -> {
            try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(inFile));
                 FileOutputStream output = new FileOutputStream(outFile)) {
                encryptionMode.encrypt(input, output);
            } catch (IOException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public void decrypt(String inFile, String outFile){
        future = service.submit(() -> {
            try (FileInputStream input = new FileInputStream(inFile);
                 FileOutputStream output = new FileOutputStream(outFile)) {
                encryptionMode.decrypt(input, output);
            } catch (IOException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public void setEncryptionMode(Mode mode, Object... objects) {
        this.encryptionMode = encryptionModes.get(mode);
        if(objects.length>0){
            settings.setInitialBlock((byte[])objects[0]);
        }
        if(objects.length>1){
            settings.setCountByte((int)objects[1]);
        }
    }

    public void setKey(byte[]key) throws IllegalArgumentException{
        this.settings.setKey(key);
    }
    public void setPadding(Padding padding) {
        settings.setPadding(padding);
    }
    public void setEncryptionType(BaseEncryption encryption) {
        settings.setEncryption(encryption);
    }
    public static void setService(ExecutorService executorService) {
        service = executorService;
    }
}
