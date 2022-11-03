package org.boozsoft.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewStringUtil {
    // 判断字符串是否为整数或者小数
    public static boolean isNumeric4(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        if(str.indexOf(".")>0){//判断是否有小数点
            if(str.indexOf(".")==str.lastIndexOf(".") && str.split("\\.").length==2){ //判断是否只有一个小数点
                return pattern.matcher(str.replace(".","")).matches();
            }else {
                return false;
            }
        }else {
            return pattern.matcher(str).matches();
        }
    }
    // 去除字符串特殊字符
    public static String regExpStr(String str){
        //1. 可以在中括号内加上任何想要删除的字符，实际上是一个正则表达式
        String regExp="[\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？-]";
        //2. 这里是将特殊字符换为空字符串,""代表直接去掉
        String replace = "";
        //3. 要处理的字符串
        str = str.replaceAll(regExp,replace);
        return str;
    }

    /**
     * 判断是否含有特殊字符
     *
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialChar(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 是否正整数
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}
