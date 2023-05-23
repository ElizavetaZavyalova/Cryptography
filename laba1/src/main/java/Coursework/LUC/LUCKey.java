package Coursework.LUC;

import lombok.*;
import Coursework.LUC.simbols.Symbol;

import java.math.BigInteger;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class LUCKey {
    BigInteger e;
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @ToString
    public class  D{
        BigInteger p;
        BigInteger q;
        BigInteger pxqx;
        BigInteger pxq_;
        BigInteger p_qx;
        BigInteger p_q_;
        private static class GSDResult{
            BigInteger k;
            BigInteger d;
            BigInteger getResult(){
                if(d.compareTo(BigInteger.ZERO)<0) {
                    return k;
                }
                return d;
            }
        }
        BigInteger gcd(BigInteger e, BigInteger s, GSDResult gSDResult){
            if (e.compareTo(BigInteger.ZERO)==0)
            {
                gSDResult.d=BigInteger.ZERO;
                gSDResult.k=BigInteger.ONE;
                return s;
            }
            GSDResult currentGSDResult=new GSDResult();
            BigInteger nod = gcd(s.mod(e),e, currentGSDResult);
            gSDResult.d=currentGSDResult.k.subtract(s.divide(e).multiply(currentGSDResult.d));
            gSDResult.k=currentGSDResult.d;
            return nod;
        }

        public void make(BigInteger e){
            pxqx=lcm(p.add(BigInteger.ONE),q.add(BigInteger.ONE));//(p+1)(q+1)
            p_qx=lcm(p.subtract(BigInteger.ONE),q.add(BigInteger.ONE));//(p-1)(q+1)
            p_q_=lcm(p.subtract(BigInteger.ONE),q.subtract(BigInteger.ONE));//(p-1)(q-1)
            pxq_=lcm(p.add(BigInteger.ONE),q.subtract(BigInteger.ONE));//(p+1)(q-1)
           GSDResult res=new GSDResult();
            gcd(e,pxqx,res);
            pxqx=res.getResult();
            gcd(e,p_qx,res);
            p_qx=res.getResult();
            gcd(e,pxq_,res);
            pxq_=res.getResult();
            gcd(e,p_q_,res);
            p_q_=res.getResult();
            /*pxqx=e.modPow(pxqx.subtract(BigInteger.ONE),pxqx);
            p_qx=e.modPow(p_qx.subtract(BigInteger.ONE),p_qx);
            pxq_=e.modPow(pxq_.subtract(BigInteger.ONE),pxq_);
            p_q_=e.modPow(p_q_.subtract(BigInteger.ONE),p_q_);*/
        }
        BigInteger lcm(BigInteger a, BigInteger b){
            return a.multiply(b).divide(a.gcd(b));//(a*b)/gcd(a,b)
        }

        public BigInteger getD(BigInteger massege){
            BigInteger D= massege.multiply(massege).subtract(BigInteger.valueOf(4)); //m*m-4
            int pD= Symbol.L(D,p);
            int pQ= Symbol.L(D,q);
            BigInteger s=lcm(p.subtract(BigInteger.valueOf(pD)),q.subtract(BigInteger.valueOf(pQ)));//(p+1)(q-1)
            GSDResult res=new GSDResult();
            gcd(e,s,res);
            s=res.d;
            return  s;
            /* if(pD==-1&&pQ==-1){
                return p_q_;
            }
            else if(pD==1&&pQ==-1){
                return pxq_;
            }
            else if(pD==-1&&pQ==1){
                return p_qx;
            }
            return pxqx;*/
        }

    }
    D d=new D();
    BigInteger n;
}
