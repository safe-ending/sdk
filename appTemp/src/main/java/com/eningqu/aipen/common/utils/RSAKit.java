package com.eningqu.aipen.common.utils;

import android.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @desc TODO
 * @author     Yanghuangping
 * @since      2018/5/17 19:47
 * @version    1.0
 *
 **/

public class RSAKit {

    private PublicKey publicKey;

    /*** 填充模式 RSA/ECB/NoPadding  RSA/ECB/PKCS1Padding */
    private static final String PADDING_MODE = "RSA/ECB/PKCS1Padding";

//    public static final String RSA = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC5CVk3l/0h5N2k95gs0sPDf5h8hyRpsERgquwfVN0lk4t5zCrgIWbhIdELA6DKQRuMZkeLkVeTODlTZgwjoV1HPVB4OXjx9ucfxEF6nYWRekhRFPipOlkRXX93d5d+BHytofJzeyN6Odo9aflB9gYmViaD6tZ3T0ahYT7H3nla9wIDAQAB";
    public static final String RSA = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDcvVLbKflXgHLqYekwOmeCvEm2uXbRtMbAtNNWp5XqYTnn4mah0F0+qlUHQ0bdzN0rAnKfBBQugtugz9F5827fnn8mHsmXe3oW8v/o/tUaUlsJuCgT27c/Md0ulKZWQT6w7PpsROIo7IGdvaWh33acWY2NdTdGuu/UFOGtqrsCIQIDAQAB";

    /*** 密钥大小*/
    private int KEY_SIZE = 1024;
    /*** 字符编码*/
    private String CHARSET = "UTF-8";

    protected Lock lock;

    public RSAKit(String publicKeyBase64) {
        this.publicKey = generatePublicKey(publicKeyBase64);
        lock = new ReentrantLock();
    }

    public String encrypt(String data) throws Exception {

        int keyByteSize = KEY_SIZE / 8;
        int maxBlockSize = keyByteSize - 11;

        byte[] dataBytes = data.getBytes(CHARSET);
        int dataLength = dataBytes.length;
        // 计算分段加密的block数 (向上取整)
        int nBlock = ( dataLength / maxBlockSize);
        if ((dataLength % maxBlockSize) != 0) {
            nBlock += 1;
        }

        ByteArrayOutputStream bos = null;

        try {

            this.lock.lock();

            Cipher cipher = Cipher.getInstance(PADDING_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            bos = new ByteArrayOutputStream(nBlock * keyByteSize);

            for (int offset = 0; offset < dataLength; offset += maxBlockSize) {
                int partLen = dataLength - offset;
                if (partLen > maxBlockSize) {
                    partLen = maxBlockSize;
                }
                // 得到分段加密结果
                byte[] cacheBlock = cipher.doFinal(dataBytes, offset, partLen);
                bos.write(cacheBlock);
            }
            bos.flush();
            return Base64Utils.encode(bos.toByteArray());
        } catch (NoSuchAlgorithmException e) {
            L.error("", e.getMessage());
        } catch (NoSuchPaddingException e) {
            L.error("", e.getMessage());
        } catch (InvalidKeyException e) {
            L.error("", e.getMessage());
        } catch (IllegalBlockSizeException e) {
            L.error("", e.getMessage());
        } catch (BadPaddingException e) {
            L.error("", e.getMessage());
        } catch (IOException e) {
            L.error("", e.getMessage());
        } finally {
            if(bos != null){
                try {
                    bos.close();
                } catch (IOException e) {
                    L.error("", e.getMessage());
                }
            }
            this.lock.unlock();
        }
        return null;
    }


    /*public String decrypt(String dataBase64){

        byte[] dataBytes = Base64.decode(dataBase64);
        int dataLength = dataBytes.length;

        int keyByteSize = KEY_SIZE / 8;
        int maxBlockSize = keyByteSize - 11;
        // 计算分段加密的block数 (向上取整)
        int nBlock = (dataLength / keyByteSize);

        ByteArrayOutputStream bos = null;

        try {

            this.lock.lock();

            Cipher cipher = Cipher.getInstance(PADDING_MODE);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            bos = new ByteArrayOutputStream(nBlock * maxBlockSize);
            for (int offset = 0; offset < dataLength; offset += keyByteSize) {
                int inputLen = dataLength - offset;
                if (inputLen > keyByteSize) {
                    inputLen = keyByteSize;
                }
                *//**得到分段解密密结果*//*
                byte[] cacheBlock = cipher.doFinal(dataBytes, offset, inputLen);
                bos.write(cacheBlock);
            }
            bos.flush();
            return StrUtil.str(bos, CHARSET);
        } catch (NoSuchAlgorithmException e) {
            logger.error("", e);
        } catch (NoSuchPaddingException e) {
            logger.error("", e);
        } catch (InvalidKeyException e) {
            logger.error("", e);
        } catch (IllegalBlockSizeException e) {
            logger.error("", e);
        } catch (BadPaddingException e) {
            logger.error("", e);
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            if(bos != null){
                try {
                    bos.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
            this.lock.unlock();
        }
        return null;
    }*/


    /**
     * 加载公钥
     *
     * @param publicKeyBase64
     * @return
     * @throws NoSuchAlgorithmException 无此算法异常
     * @throws InvalidKeySpecException
     */
    private PublicKey generatePublicKey(String publicKeyBase64) {
        PublicKey publicKey = null;
        try {
            byte[] buffer = Base64.decode(publicKeyBase64, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            publicKey = keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            L.error("", e.getMessage());
        } catch (InvalidKeySpecException e) {
            L.error("", e.getMessage());
        }
        return publicKey;
    }
}
