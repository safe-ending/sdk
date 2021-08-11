package com.eningqu.aipen.common.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MCloudAESUtil {
    private static final char[] HEXDIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static List<Integer> pinttype2Md5 = new ArrayList();

    static {
        pinttype2Md5.add(1);
        pinttype2Md5.add(2);
        pinttype2Md5.add(3);
        pinttype2Md5.add(4);
        pinttype2Md5.add(5);
        pinttype2Md5.add(6);
        pinttype2Md5.add(8);
        pinttype2Md5.add(10);
        pinttype2Md5.add(13);
        pinttype2Md5.add(14);
        pinttype2Md5.add(15);
        pinttype2Md5.add(16);
    }

    public MCloudAESUtil() {
    }

    public static void main(String[] args) throws Exception {
        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "input.conf";
        System.out.println("the config file path is " + filePath + "\r\n");
        Properties p = new Properties();
        InputStream in = null;
        in = new FileInputStream(filePath);
        p.load(in);
        in.close();
        String assignedKey = p.getProperty("assignedKey");
        String inputPassword = p.getProperty("inputPassword");
        int pintype = Integer.parseInt(p.getProperty("pintype"));
        String aasResult = p.getProperty("aasResult");
        System.out.println(decode(aasResult, assignedKey, inputPassword, pintype));
    }

    public static String decode(String encryptedString, String assignedKey, String inputPassword, int pintype) throws Exception {
        if (pinttype2Md5.contains(pintype)) {
            inputPassword = byte2hex(md5encrypt(inputPassword));
        }

        byte[] md5encryptKey = md5encrypt(inputPassword + assignedKey);
        String keys = byte2hex(md5encryptKey);
        keys = keys.toUpperCase();
        byte[] secretKey = Arrays.copyOfRange(keys.getBytes(), 0, 16);
        byte[] resultBytes = hex2byte(encryptedString);
        SecretKeySpec secKey = new SecretKeySpec(secretKey, "AES");
        Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        aesCipher.init(2, secKey);
        byte[] bytes = aesCipher.doFinal(resultBytes);
        String decryptText = new String(bytes);
        return decryptText;
    }

    private static byte[] md5encrypt(String data) throws NoSuchAlgorithmException {
        MessageDigest mdTemp = MessageDigest.getInstance("MD5");
        mdTemp.update(data.getBytes());
        return mdTemp.digest();
    }

    private static String byte2hex(byte[] tmp) {
        char[] str = new char[tmp.length * 2];
        int k = 0;

        for(int i = 0; i < tmp.length; ++i) {
            byte byte0 = tmp[i];
            str[k++] = HEXDIGITS[byte0 >>> 4 & 15];
            str[k++] = HEXDIGITS[byte0 & 15];
        }

        return new String(str);
    }

    private static byte[] hex2byte(String s) {
        byte[] b = s.getBytes();
        byte[] b2 = new byte[b.length / 2];

        for(int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte)Integer.parseInt(item, 16);
        }

        return b2;
    }
}
