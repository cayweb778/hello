package org.boozsoft.util;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDate;

/**
 *
 */
public class PdfUtil {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PdfUtil.class);

    //经过测试,dpi为96,100,105,120,150,200中,105显示效果较为清晰,体积稳定,dpi越高图片体积越大,一般电脑显示分辨率为96
    public static final int DEFAULT_DPI = 105;
    //默认转换的图片格式为jpg
    public static final String DEFAULT_FORMAT = ".png";

//
//    public static void main(String[] args) throws IOException {
//
//    }


    /***
     * PDF文件转PNG图片，全部页数
     *
     * @param filePath pdf完整路径
     * @param fileName 图片存放的文件夹
     * @param dpi dpi越大转换后越清晰，相对转换速度越慢
     * @param in FileInputStream
     * @return
     */

    public static void pdfToImage(String filePath, String fileName, int dpi, FileInputStream in) {
        PDDocument pdDocument = new PDDocument();
        try {
            pdDocument = PDDocument.load(in);
            PDFRenderer renderer = new PDFRenderer(pdDocument);
            /* dpi越大转换后越清晰，相对转换速度越慢 */
            int pages = pdDocument.getNumberOfPages();
            BufferedImage image = renderer.renderImageWithDPI(0, dpi);
            InputStream inputStream = bufferedImageToInputStream(image);
            FtpUtil.uploadFile(filePath, fileName, inputStream);
            in.close();
            System.out.println("PDF文档转PNG图片成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                pdDocument.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean createDirectory(String folder) {
        File dir = new File(folder);
        if (dir.exists()) {
            return true;
        } else {
            return dir.mkdirs();
        }
    }

    /**
     * 将BufferedImage转换为InputStream
     * @param image
     * @return
     */
    public static InputStream bufferedImageToInputStream(BufferedImage image){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", os);
            InputStream input = new ByteArrayInputStream(os.toByteArray());
            return input;
        } catch (IOException e) {
        }
        return null;
    }


}
