package com.eningqu.aipen.common.utils;

import java.util.Random;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2021/7/6 18:38
 * desc   : 生成随机字符串
 * version: 1.0
 */
public class GeneratorUtil {
    private static Random random = new Random();
    private static final char ALPHA = 'a';
    private static final char NUMBER = '0';
    private static final char UPPERCASEALPHA = 'A';
    private static final int ALPHABOUND = 26;
    private static final int NUMBERBOUND = 10;
    private static final int RANDOMTYPEBOUND = 3;

    public static String randomSequence(int size){
        StringBuffer sequence = new StringBuffer();
        for(int i=0;i<size;i++){
            sequence.append(getRandomValue());
        }
        return sequence.toString();
    }

    private static char getRandomValue(){
        int index = random.nextInt(RANDOMTYPEBOUND);
        if(index == 0){
            return randomAlpha();
        }else if(index == 1){
            return randomNumber();
        }else{
            return randomUpperAlpha();
        }
    }

    private static char randomAlpha(){
        return randomValue(ALPHA,ALPHABOUND);
    }

    private static char randomNumber(){
        return randomValue(NUMBER,NUMBERBOUND);
    }

    private static char randomUpperAlpha(){
        return randomValue(UPPERCASEALPHA,ALPHABOUND);
    }

    private static char randomValue(int start,int bound){
        int index = random.nextInt(bound);
        char next = (char)(start + index);
        return next;
    }

    public static void main(String[] args) {
        for(int i=0;i<10;i++){
            System.out.println(randomSequence(32));
        }
    }
}
