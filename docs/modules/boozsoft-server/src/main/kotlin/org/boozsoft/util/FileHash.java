package org.boozsoft.util;
import org.apache.commons.codec.digest.DigestUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * file hash
 */
public class FileHash {

//    public static void main(String[] args) {
//        try {
//            //此处我测试的是我本机jdk源码文件的MD5值
//            String filePath1 = "D://awe.xlsx";
//            String filePath2 = "D://www.xlsx";
//            String s1 = DigestUtils.md5Hex(new FileInputStream(filePath1));
//            String s = DigestUtils.md5Hex(new FileInputStream(filePath2));
//            System.out.println(s1 + "：文件的md5值");
//            System.out.println(s + "：文件的md5值");
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    public static String getFileHash(String filePath) throws IOException {
        return DigestUtils.md5Hex(new FileInputStream(filePath));
    }



}