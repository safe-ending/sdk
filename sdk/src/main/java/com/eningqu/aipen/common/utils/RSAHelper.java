package com.eningqu.aipen.common.utils;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * 说明：
 * 作者：WangYabin
 * 邮箱：wyb@eningqu.com
 * 时间：15:46
 */
public class RSAHelper  {
    /**RSA算法*/
    public static final String RSA = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC5CVk3l/0h5N2k95gs0sPDf5h8hyRpsERgquwfVN0lk4t5zCrgIWbhIdELA6DKQRuMZkeLkVeTODlTZgwjoV1HPVB4OXjx9ucfxEF6nYWRekhRFPipOlkRXX93d5d+BHytofJzeyN6Odo9aflB9gYmViaD6tZ3T0ahYT7H3nla9wIDAQAB";
    /**加密方式，android的*/
    public static final String TRANSFORMATION = "RSA/None/NoPadding";
//    public static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    /**加密方式，标准jdk的*/
   /* public static final String TRANSFORMATION = "RSA/None/PKCS1Padding";*/
    private static KeyFactory keyStore;
    /** 使用公钥加密 */
    public static byte[] encryptByPublicKey(byte[] data, byte[] publicKey) throws Exception {
        // 得到公钥对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
         keyStore = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyStore.generatePublic(keySpec);
        // 加密数据
        Cipher cp = Cipher.getInstance(TRANSFORMATION);
        cp.init(Cipher.ENCRYPT_MODE, pubKey);
        return cp.doFinal(data);
    }

    public  static byte[] processData(byte[] encryptedData, Key key){

        //用来保存处理结果
        byte[] resultBytes = null;
        try {
            //构建Cipher对象，需要传入一个字符串，格式必须为"algorithm/mode/padding"或者"algorithm/",意为"算法/加密模式/填充方式"
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            //初始化Cipher，mode指定是加密还是解密，key为公钥或私钥
            cipher.init(Cipher.ENCRYPT_MODE,key);
            //处理数据
            int inputLen = encryptedData.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > 128) {
                    cache = cipher.doFinal(encryptedData, offSet, 128);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * 128;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();
//            resultBytes = cipher.doFinal(decryptedData);
            return decryptedData;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }
        return null;
    }

    public static PublicKey keyStrToPublicKey(String publicKeyStr){

        PublicKey publicKey = null;

        byte[] keyBytes = Base64.decode(publicKeyStr,Base64.DEFAULT);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

        try {

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            publicKey = keyFactory.generatePublic(keySpec);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return publicKey;

    }

    /** 使用私钥解密 */
    public static byte[] decryptByPrivateKey(byte[] encrypted, byte[] privateKey) throws Exception {
        // 得到私钥对象
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory kf = KeyFactory.getInstance(RSA);
        PrivateKey keyPrivate = kf.generatePrivate(keySpec);
        // 解密数据
        Cipher cp = Cipher.getInstance(TRANSFORMATION);
        cp.init(Cipher.DECRYPT_MODE, keyPrivate);
        byte[] arr = cp.doFinal(encrypted);
        return arr;
    }
}
