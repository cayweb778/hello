package org.boozsoft.util;

import cn.hutool.extra.ftp.Ftp;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;

import java.io.*;
import java.util.Base64;
import java.util.Objects;


/**
 * @Description: FTP服务器上传下载文件工具类
 * @Author: ruanyanghui
 * @Date: 2019/8/27 15:53
 */
public class FtpUtil {
    //ftp服务器ip地址
//    @Value("${ftp.host}")
    public static String host = "81.70.32.227";
    //端口号
//    @Value("${ftp.port}")
    public static int port = 21;
    //用户名
//    @Value("${ftp.username}")
    public static String username = "booz";
    //密码
//    @Value("${ftp.password}")
    public static String password = "booz123";
    //图片根路径
//    @Value("${ftp.filepath}")
    public static String path = "/ncpz";

    //本地字符编码
    private static String LOCAL_CHARSET = "GBK";

    // FTP协议里面，规定文件名编码为iso-8859-1
    private static String SERVER_CHARSET = "ISO-8859-1";

    /**
     * Description: 向FTP服务器上传文件
     * 两个文件夹：file、temp
     * 1、所有文件上传 file
     * 2、转成图片的放到temp
     *
     * @param filePath FTP服务器文件存放路径。例如：nc/fil/...
     * @param filename 上传到FTP服务器上的文件名
     * @param input    本地要上传的文件的 输入流
     * @return 成功返回true，否则返回false
     */
    public static boolean uploadFile(String filePath, String filename, InputStream input) {

        boolean result = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(host, port);// 连接FTP服务器
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return result;
            }
            ftp.enterLocalPassiveMode();
            //切换到上传目录
            if (!ftp.changeWorkingDirectory(path + filePath)) {
                String[] dirs = filePath.split("/");
                String tempPath = path;
                ftp.makeDirectory(tempPath);
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir)) continue;
                    tempPath += "/" + dir;
                    ftp.makeDirectory(tempPath);
                }
                ftp.changeWorkingDirectory(tempPath);
            }
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.storeFile(new String(filename.getBytes(LOCAL_CHARSET), SERVER_CHARSET), input);
            input.close();
            ftp.logout();
            result = true;
        } catch (IOException e) {

        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return result;
    }

    /**
     * Description: 从FTP服务器下载文件
     *
     * @param remotePath FTP服务器上的相对路径
     * @param fileName   要下载的文件名
     * @param localPath  下载后保存到本地的路径
     * @return
     */
    public static boolean downloadFile(String remotePath, String fileName, String localPath) {
        boolean result = false;
        FTPClient ftp = new FTPClient();
        File file = new File(localPath);
        try {
            int reply;
            ftp.connect(host, port);
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return result;
            }
            if (!file.exists()) {
                file.mkdirs();
            }
            ftp.changeWorkingDirectory(remotePath);// 转移到FTP服务器目录
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                //解决中文乱码问题，两次解码
                byte[] bytes = ff.getName().getBytes("ISO-8859-1");
                String getfileName = new String(bytes, "GBK");
                if (getfileName.equals(fileName)) {
                    File localFile = new File(localPath + "/" + fileName.split("booz")[1]);
                    OutputStream is = new FileOutputStream(localFile);
                    ftp.retrieveFile(ff.getName(), is);
                    is.close();
                }
            }

            ftp.logout();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return result;
    }

    public static InputStream downloadFile2(String remotePath, String fileName) {
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(host, port);
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            int reply = ftp.getReplyCode();
            ftp.changeWorkingDirectory(remotePath);// 转移到FTP服务器目录
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                //解决中文乱码问题，两次解码
                byte[] bytes = ff.getName().getBytes("ISO-8859-1");
                String getfileName = new String(bytes, "GBK");
                if (getfileName.equals(fileName)) {
                    InputStream inputStream = ftp.retrieveFileStream(ff.getName());
                    return inputStream;
                }
            }
            ftp.logout();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /* ftp读取图片并转Base64 */
    public static String ImgToBase64(String ftpUrl, String imgName) {
        FTPClient ftpClient = new FTPClient();
        InputStream inputStream = null;
        String re = "data:image/png;base64,";
        try {
            ftpClient.connect(host, port);
            ftpClient.login(username, password);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            //是否成功登录服务器
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
            }
            //跳转到指定目录
            ftpClient.changeWorkingDirectory(ftpUrl);
            // 开通单独端口通道传输数据 linux上，由於安全限制，可能某些端口沒有開啟，所以就出現阻塞
            ftpClient.enterLocalPassiveMode();
            //5.遍历下载的目录
            FTPFile[] fs = ftpClient.listFiles();

            for (FTPFile ff : fs) {
                //解决中文乱码问题，两次解码
                byte[] bytes = ff.getName().getBytes("ISO-8859-1");
                String fileName = new String(bytes, "GBK");
                if (imgName.equals(fileName)) {
                    inputStream = ftpClient.retrieveFileStream(ff.getName());
                }
            }

            if (inputStream != null) {
                byte[] data = null;
                ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
                byte[] buff = new byte[100];
                int rc = 0;
                while ((rc = inputStream.read(buff, 0, 100)) > 0) {
                    swapStream.write(buff, 0, rc);
                }
                data = swapStream.toByteArray();
                Base64.Encoder encoder = Base64.getEncoder();
                re += encoder.encodeToString(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return re;
    }

    public static boolean Base64ToImg(String imgStr, String imgFilePath) {
        if (imgStr == null) // 图像数据为空
            return false;
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            // Base64解码
            byte[] bytes = decoder.decode(imgStr);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }
            // 生成jpeg图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(bytes);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return boolean
     * @Author 删除文件
     * @Date 11:15 2020/10/14
     * @Param [remotePath, fileName]
     **/
    public static boolean delFile(String remotePath, String fileName) {
        boolean result = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(host, port);
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return result;
            }
            ftp.changeWorkingDirectory(remotePath);// 转移到FTP服务器目录
            FTPFile[] ftpFiles = ftp.listFiles();
            ftp.deleteFile(new String(fileName.getBytes(LOCAL_CHARSET), SERVER_CHARSET));
            ftp.logout();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return result;
    }


    /**
     * @return boolean
     * @Author 删除FTP空文件夹
     * @Date 17:35 2020/10/13
     * @Param remotePath
     **/
    public static boolean delDir(String remotePath) {
        boolean result = false;
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(host, port);
            ftp.login(username, password);// 登录
            int reply = ftp.getReplyCode();
            ftp.removeDirectory(remotePath);
            result = true;
            ftp.logout();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除文件夹及其文件夹下的所有文件
     * cn.hutool.extra.ftp.AbstractFtp
     *
     * @param path
     */
    public static Boolean delAllFile(String path) {
        boolean b = false;
        Ftp ftp = null;
        try {
            ftp = new Ftp(host, port, username, password);
            b = ftp.delDir(path);
            ftp.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(ftp)) {
                try {
                    ftp.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return b;
    }

    /**
     * base64转inputStream
     *
     * @param base64string
     * @return
     */
    public static InputStream baseToInputStream(String base64string) {
        ByteArrayInputStream stream = null;
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] bytes1 = decoder.decode(base64string.split(",")[1]);
            stream = new ByteArrayInputStream(bytes1);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return stream;
    }

    public static String GetImageStr(String imgFilePath) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(imgFilePath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(data);
        // 返回Base64编码过的字节数组字符串
    }

    public static boolean GenerateImage(String imgStr, String imgFilePath) {
        // 对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null)
            // 图像数据为空
            return false;
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            // Base64解码
            byte[] bytes = decoder.decode(imgStr);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }
            // 生成jpeg图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(bytes);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


//    public static void main(String[] args) {
//        System.out.println(GenerateImage("","E:\\外星人.jpg"));

//        boolean b = delDir2("baoxiao/bx_454209/JK/2020/JK_20201013172302/img");
//        System.out.println(b);
//        try {
////            FileInputStream in=new FileInputStream(new File("C:\\阿三.jpg"));
////            boolean flag = uploadFile("aaa", "阿三.jpg", in);
//            downloadFile2("","",null);
//            boolean flag = downloadFile("baoxiao/excel","CG20201110002booz商品信息模板.xls", "D://booz");
////            boolean flag = delFile("192.168.8.132", 21, "test", "test", "test/img","1.jpg", "D://");
//            System.out.println(flag);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

}
