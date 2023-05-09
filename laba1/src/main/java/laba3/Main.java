package laba3;
import laba3.rsa.RSA;
import laba3.rsa.RSAKey;
import laba3.rsa.SimplicityMode;
import laba3.winnerAttack.WinnerAttack;
import java.math.BigInteger;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        RSA rsa=new RSA(SimplicityMode.solovayStrassen, 0.97F,5);
        System.out.println("--------------------------Ключи-------------------------------");
        System.out.println(rsa.generateKey());
        System.out.println("--------------------------текст-------------------------------");
        int count=9000;
        byte[] value=new byte[count];
        Random rnd=new Random();
        for(int i=0; i<count; i++){
            value[i]=(byte)(rnd.nextInt()&0xFF);
        }
        for(int i=0;i<value.length;i++){
            System.out.print(value[i]+" ");
        }
        System.out.print("\n ");
        System.out.println("--------------------------Шифро текст-------------------------------");
         byte[] b=rsa.encrypt(value);
        for(int i=0;i<b.length;i++){
            System.out.print(b[i]+" ");
        }
        System.out.print("\n ");
        System.out.println("--------------------------Расшифровка-------------------------------");
         b=rsa.decrypt(b);
         for(int i=0;i<b.length;i++){
             System.out.print(b[i]+" ");
         }
        System.out.print("\n ");
        System.out.println("--------------------------Атака виннера-------------------------------");
        WinnerAttack winnerAttack=new WinnerAttack();
        BigInteger e=new BigInteger(new String("3941153"));
        BigInteger n=  new BigInteger(new String("13407379"));
        System.out.println("e:"+e);
        System.out.println("n:"+n);
        System.out.println(winnerAttack.makeAttack(e ,n));

    }
}