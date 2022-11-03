package org.boozsoft.util;

import java.util.regex.Pattern;

/**
 * 正则表达式
 */
public class PatternUtil {


    private  static Pattern getNum = Pattern.compile("[^0-9]");;

    private  static Pattern getStr = Pattern.compile("[\\d]");

    /**
     * 只保留数字
     * @param str
     * @return
     */
    public static  String getNum(String str){
        return getNum.matcher(str).replaceAll("");
    }

    /**
     * 去数字
     * @param str
     * @return
     */
    public static String getStr(String str){
        return  getStr.matcher(str).replaceAll("");
    }
}
