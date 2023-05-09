package laba3.rsa;

import laba3.tests.FarmTest;
import laba3.tests.MillerRabinTest;
import laba3.tests.SimplicityCheck;
import laba3.tests.SolovayStrassenTest;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RSA {
    static class GenerateKeys {
        Map<SimplicityMode, SimplicityCheck> simplicityModes = new HashMap<>();
        Random random=new Random();
        @Setter
        SimplicityMode testMode;
        @Setter
        int bitLength;

        @Setter
        float probability;
        public GenerateKeys(SimplicityMode testMode, float probability, int biteLength){
            setProbability(probability);
            setTestMode(testMode);
            setBitLength(biteLength);
            simplicityModes.put(SimplicityMode.farm,new FarmTest());
            simplicityModes.put(SimplicityMode.millerRabin,new MillerRabinTest());
            simplicityModes.put(SimplicityMode.solovayStrassen,new SolovayStrassenTest());

        }
        BigInteger generateBigInteger(){
            return new BigInteger(bitLength,random).setBit(bitLength-1).setBit(0);
        }
        boolean isBigIntegerSizeCorrect(BigInteger number){
            int b=number.bitLength();
            return number.bitLength()==bitLength;
        }
        //ed+kF(n)=gcd(e,F(n))=1
        //d>(1/3)N^(1/4) для Винера
        //e различны
        BigInteger generateSimpleBigInteger(BigInteger generate){
            while(!simplicityModes.get(testMode).isSimple(generate,probability)){
                generate=generate.add(BigInteger.TWO);
                if(!isBigIntegerSizeCorrect(generate)){
                    generate=BigInteger.ONE.shiftLeft(bitLength-1).setBit(0);
                }
            }
            return generate;
        }
        public RSAKey generateRSAKey(){
            RSAKey rSAKey=new RSAKey();
            BigInteger p=(generateSimpleBigInteger(generateBigInteger()));
            int addLength= random.nextInt(bitLength>>1,(bitLength-1));
            BigInteger addNum=new BigInteger(addLength,random).setBit(addLength>>2-1).clearBit(0);
            BigInteger q=(generateSimpleBigInteger(p.add(addNum)));
            rSAKey.setN(p.multiply(q));
            BigInteger thi=makeThi(p,q);
            makeKey(rSAKey,thi);
            return  rSAKey;

        }
        BigInteger makeThi(BigInteger p, BigInteger q){
            return p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        }
        boolean isDProtectedFromAttack(BigInteger d, BigInteger minD){
            return d.compareTo(minD)>0;
        }
        void makeKey(RSAKey rsaKey, BigInteger thi){
            GSDResult gSDResult=new GSDResult();
            BigInteger minD=rsaKey.getN().sqrt().sqrt().divide(BigInteger.valueOf(3));
            BigInteger e;
            do{
                e=generateED(gSDResult,thi);
            }while(!isDProtectedFromAttack(gSDResult.d,minD));
            rsaKey.setE(e);
            rsaKey.setD(gSDResult.d);
            rsaKey.setThi(thi);
        }
        BigInteger generateED(GSDResult gSDResult, BigInteger thi){
            BigInteger e=generateE(thi);
            while((!gcd(e,thi,gSDResult).equals(BigInteger.ONE))){
                e=generateE(thi);
            }
            return e;
        }

        public BigInteger generateE(BigInteger thi){
           int oneCount= random.nextInt(1,16);
            BigInteger e=BigInteger.ONE.shiftLeft(thi.bitLength()).setBit(0);
            for(int i=0; i<oneCount; i++) {
                e = e.setBit(random.nextInt(2, thi.bitLength() - 1));
            }
            return e;
        }

        private static class GSDResult{
            BigInteger k;
            BigInteger d;
        }
        BigInteger gcd(BigInteger e, BigInteger thi, GSDResult gSDResult){
            if (e.equals(BigInteger.ZERO))
            {
                gSDResult.d=BigInteger.ZERO;
                gSDResult.k=BigInteger.ONE;
                return thi;
            }
            GSDResult currentGSDResult=new GSDResult();
            BigInteger nod = gcd(thi.mod(e),e, currentGSDResult);
            gSDResult.d=currentGSDResult.k.subtract(thi.divide(e).multiply(currentGSDResult.d));
            gSDResult.k=currentGSDResult.d;
            return nod;
        }


    }

    GenerateKeys generateKeys;
    @Getter
    RSAKey rSAKey=null;
    public RSA(SimplicityMode testMode, float probability, int bitLength){
        assert probability>0.5;
        assert bitLength >=5;//так как 1_0000^2=1_0000_0000 а шифровние идет блоками побайтово
        generateKeys= new GenerateKeys(testMode, probability, bitLength);
    }
    public RSAKey  generateKey(){
        rSAKey=generateKeys.generateRSAKey();
        return rSAKey;

    }
    public byte[] encrypt(byte[] bytes){
       //Решила шифровать масив bites. по n байт;
        int count=(rSAKey.n.bitLength()/8);
        int nCount=(rSAKey.n.bitLength())/8+1;
        int blockCount=bytes.length%count==0?(bytes.length/count):bytes.length/count+1;
        BigInteger e=rSAKey.getE();
        if(e.compareTo(BigInteger.valueOf(blockCount))<=0){
            ///e+k*F(n)>blockCount
            ///blockCount-e<k*F(n)
            //k=(blockCount-e)/F(n)+1
            BigInteger k=BigInteger.valueOf(blockCount).subtract(e).divide(rSAKey.getThi()).add(BigInteger.ONE);
            e=e.add(k.multiply(rSAKey.getThi()));//защита от атаки хостада
        }
        byte[]result=new byte[(nCount)*(blockCount)];
        int index=0;
        for(int i=0; i<bytes.length;i+=count){
            if(bytes.length-i<count){
                count=bytes.length-i;
            }
            BigInteger encryptNum=new BigInteger(1,bytes,i,count);
            encryptNum=encryptNum.modPow(e,rSAKey.getN());
            byte[] encryptBytes=encryptNum.toByteArray();
            System.arraycopy(encryptBytes, 0, result, ((index+1)*nCount)-encryptBytes.length,(encryptBytes.length));
            index++;
        }
        return result;
    }
    public byte[] decrypt(byte[] bytes){
        int nCount=(rSAKey.n.bitLength())/8+1;
        int count=(rSAKey.n.bitLength()/8);
        BigInteger encryptNum=new BigInteger(1,bytes,bytes.length-nCount,nCount);
        encryptNum=encryptNum.modPow(rSAKey.getD(),rSAKey.getN());
        byte[] encryptBytes=encryptNum.toByteArray();// возвращает больше на один байт если предыдущие заполнены полностью.. т.е лишний 0 байт
        int srcPos=(encryptBytes[0]==0)?(1):(0);
        int countShift= (encryptBytes[0]==0)?(encryptBytes.length-1):(encryptBytes.length);
        byte[]result=new byte[(count)*(bytes.length/nCount-1)+countShift];
        System.arraycopy(encryptBytes, srcPos, result, (count)*(bytes.length/nCount-1),countShift);
        int index=0;
        for(int i=0; i<bytes.length-nCount;i+=nCount){
            encryptNum=new BigInteger(1,bytes,i,nCount);
            encryptNum=encryptNum.modPow(rSAKey.getD(),rSAKey.getN());
            encryptBytes=encryptNum.toByteArray();
            srcPos=encryptBytes.length>count?(encryptBytes.length-count):0;
            countShift= Math.min(encryptBytes.length, count);
            System.arraycopy(encryptBytes, srcPos, result, index*count,countShift);
            index++;
        }
        return result;
    }
}
