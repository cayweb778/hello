package org.boozsoft.util;

import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 随机生成6位
 */
public class RandomNum {
    public static String num(){
        Random random = new Random();
        String result="";
        for (int i=0;i<8;i++){
            result+=random.nextInt(10);
        }
        return result;
    }

    public static String uuid(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }

    /**
     * 使用正则表达式来判断字符串中是否包含字母
     * @param str 待检验的字符串
     * @return 返回是否包含
     * true: 包含字母 ;false 不包含字母
     */
    public static boolean judgeContainsStr(String str) {
        String regex=".*[a-zA-Z]+.*";
        Matcher m= Pattern.compile(regex).matcher(str);
        return m.matches();
    }

}
