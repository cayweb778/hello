package org.boozsoft.rest;//package org.boozsoft.rest;

import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.ocr.v20181119.OcrClient;
import com.tencentcloudapi.ocr.v20181119.models.MixedInvoiceItem;
import com.tencentcloudapi.ocr.v20181119.models.MixedInvoiceOCRRequest;
import com.tencentcloudapi.ocr.v20181119.models.MixedInvoiceOCRResponse;
import com.tencentcloudapi.ocr.v20181119.models.SingleInvoiceInfo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.utils.StringUtils;
import org.boozsoft.domain.entity.FtpFile;
import org.boozsoft.domain.entity.SysCheckApi;
import org.boozsoft.domain.entity.SysUsage;
import org.boozsoft.domain.entity.invoice.InvoiceCheckRecord;
import org.boozsoft.domain.vo.TentcentOCRInfoVO;
import org.boozsoft.domain.vo.TentcentOCRVO;
import org.boozsoft.repo.*;
import org.boozsoft.service.OCRService;
import org.boozsoft.util.*;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 票据识别控制层
 */
@Slf4j
@RestController
@RequestMapping("/ocr")
@Api(value = "电子发票", tags = "API系统：电子发票")
public class OCRController {

    @Autowired
    OCRService ocrService;
    @Autowired
    SysOcrApiRepository sysOcrApiRepository;
    @Autowired
    FtpFileRepository ftpFileRepository;
    @Autowired
    SysUsageRepository sysUsageRepository;
    @Autowired
    InvoiceCheckRecordRepository invoiceCheckRecordRepository;
    @Autowired
    SysCheckApiRepository sysCheckApiRepository;


    /**
     * 混合票据识别
     *
     * @return
     */
    @PostMapping("/listOCR")
    public Mono<R> listOCR(@RequestPart("file") FilePart filePartParm, ArrayList<TentcentOCRVO> list) throws IOException {
        Path tempFilePath = Files.createTempFile("", filePartParm.filename());
        String tempFileName = tempFilePath.getFileName().toString();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).replaceAll(" ", "").replaceAll("-", "").replaceAll(":", "");
        String suffix = tempFileName.substring(tempFileName.lastIndexOf("."));
        return Mono.just(filePartParm)
                // 上传到临时目录 
                .flatMap(filePart -> {
                    try {
                        return DataBufferUtils
                                .write(filePart.content(), AsynchronousFileChannel.open(tempFilePath, StandardOpenOption.WRITE), 0)
                                .doOnComplete(() -> log.info("上传成功"))
                                .collectList()
                                .map(item -> tempFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Mono.just("");
                })
                // 存到文件上传记录，方便FTP查找
                .map(item -> {
                    int size = 0;

                    FtpFile ftpFile = new FtpFile();
                    try {
                        String imgPath = "fp/file";
                        File tempFileObject = new File(tempFilePath.toString());
                        FileInputStream in = new FileInputStream(tempFileObject);
                        size = in.available();
                        FtpUtil.uploadFile(imgPath, "ocr_"+time + "_" + filePartParm.filename(), in);
                        ftpFile.setUrl("ncpz/" + imgPath);
                        ftpFile.setType(suffix);
                        ftpFile.setSize(String.valueOf(size));
                        ftpFile.setNewName("ocr_"+time + "_" + filePartParm.filename());
                        ftpFile.setOldName(filePartParm.filename());
                        ftpFile.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        //pdf转图片 
                        if(suffix.contains("pdf")){
                            FileInputStream ins = new FileInputStream(tempFileObject);
                            ftpFile.setNewName("ocr_"+time + "_" + filePartParm.filename().replace(suffix,".png"));
                            PdfUtil.pdfToImage(imgPath, ftpFile.getNewName(), 75, ins);
                            //ftpFile.setBeiyong1()
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        assert tempFilePath != null;
                        try {
                            Files.delete(tempFilePath);
                        } catch (IOException e) {

                        }
                    }
                    return ftpFile;
                })
                // 获取配置的OCRAPI接口
                .flatMap(ftp -> {
                    return sysOcrApiRepository.findAll().collectList()
                            .map(ocr -> {
                                Credential cred = new Credential(ocr.get(0).getSecretId(), ocr.get(0).getSecretKey());
                                HttpProfile httpProfile = new HttpProfile();
                                httpProfile.setEndpoint("ocr.tencentcloudapi.com");
                                ClientProfile clientProfile = new ClientProfile();
                                clientProfile.setHttpProfile(httpProfile);
                                OcrClient client = new OcrClient(cred, "ap-beijing", clientProfile);
                                /* ftp服务器图片转base64 */
                                String base64Str = FtpUtil.ImgToBase64(ftp.getUrl(), ftp.getNewName());
                                String pamas = "{\"ImageBase64\":\"" + base64Str + "\"}";
                                MixedInvoiceOCRRequest req = MixedInvoiceOCRRequest.fromJsonString(pamas, MixedInvoiceOCRRequest.class);
                                MixedInvoiceOCRResponse resp = null;

                                try {
                                    resp = client.MixedInvoiceOCR(req);
                                } catch (TencentCloudSDKException e) {
                                    e.printStackTrace();
                                }

                                String json = MixedInvoiceOCRResponse.toJsonString(resp);

                                MixedInvoiceItem[] mixedInvoiceItems = resp.getMixedInvoiceItems();
                                String finalBase64Str = base64Str;
                                Arrays.stream(mixedInvoiceItems).forEach(j -> {
                                    SingleInvoiceInfo[] singleInvoiceInfos = j.getSingleInvoiceInfos();
                                    TentcentOCRVO detail = new TentcentOCRVO();
                                    detail.setRect(j.getRect());
                                    detail.setBase64Image(finalBase64Str);
                                    detail.setFileId(ftp.getId());
                                    detail.setFtpFile(ftp);
                                    detail.setInvoiceJson(json);
                                    detail.setDjStatus("2");
                                    detail.setRzStatus("2");
                                    detail.setFpStatus("1");

                                    List<TentcentOCRInfoVO> lissst1 = new ArrayList<>();
                                    List<String> list1 = new ArrayList<>();
                                    List<String> list2 = new ArrayList<>();
                                    List<String> list3 = new ArrayList<>();
                                    List<String> list4 = new ArrayList<>();
                                    List<String> list5 = new ArrayList<>();
                                    List<String> list6 = new ArrayList<>();
                                    List<String> list7 = new ArrayList<>();
                                    List<String> list8 = new ArrayList<>();

                                    Arrays.stream(singleInvoiceInfos).forEach(k -> {
                                        // 打印发票识别信息
//                                        System.out.println(k.getName()+"\t"+k.getValue());
                                        if(k.getName().equals(OcrKey.OCR_CHUFASHIJIAN) || k.getName().equals(OcrKey.OCR_GUOLU_RIQI) || k.getName().equals(OcrKey.INVOICE_DATE)){
                                            //保留年月
                                            String dateStr = "";
                                            if (k.getValue().contains("日")) {
                                                String[] str = k.getValue().split("日");
                                                dateStr = str[0].replace("年", "-").replace("月", "-").replace("日", "");
                                            } else {
                                                dateStr = k.getValue();
                                            }
                                            detail.setTravelDate(dateStr);

                                        }else if(k.getName().equals(OcrKey.OCR_CHUFAZHAN) || k.getName().equals(OcrKey.OCR_GUOLU_CHUFAHZNA) || k.getName().equals(OcrKey.OCR_SF)){
                                            detail.setStrAddress(k.getValue());
                                        }else if(k.getName().equals(OcrKey.OCR_DAODAZHAN) || k.getName().equals(OcrKey.OCR_GUOLU_RUKOUZHAN) || k.getName().equals(OcrKey.OCR_MD)){
                                            detail.setEndAddress(k.getValue());
                                        }else if(k.getName().equals(OcrKey.OCR_XINGMING) || k.getName().equals(OcrKey.OCR_SK)){
                                            detail.setTravelName(k.getValue());
                                        }else if(k.getName().equals(OcrKey.OCR_XIBIE) || k.getName().equals(OcrKey.INVOICE_XIBIE) ){
                                            detail.setXtype(k.getValue());
                                        }else if(k.getName().equals(OcrKey.OCR_BIANHAO) || k.getName().equals(OcrKey.OCR_DZKPHM) ){
                                            detail.setCodeNum(k.getValue());
                                        }else{

                                            switch (k.getName()) {
                                                case OcrKey.INVOICE_CODE:// 发票代码
                                                    detail.setCodeNum(k.getValue().replace("No", ""));
                                                    detail.setInvoiceCode(k.getValue().replace("No", ""));
                                                    break;
                                                case OcrKey.INVOICE_NUM:// 发票代码 + 发票号码
                                                    //detail.setCodeNum(detail.getCodeNum().replace("No", "") + k.getValue().replace("No", ""));
                                                    detail.setInvoiceNum(k.getValue().replace("No", ""));
                                                    break;
                                                case OcrKey.BILLNAME:
                                                    detail.setBillName(k.getValue());
                                                    detail.setYwType("1");
                                                    if (k.getValue().contains("专")) {
                                                        detail.setYwType("2");
                                                    }
                                                    break;
                                                case OcrKey.TAXNUM:
                                                    detail.setTaxNum(k.getValue());
                                                    break;
                                                case OcrKey.OCR_HJJE: // 合计金额
                                                    detail.setBxMoney(k.getValue());
                                                    detail.setSbMoney(k.getValue());
                                                    detail.setHjje(k.getValue());
                                                    break;
                                                case OcrKey.OCR_HJSE:
                                                    detail.setHjse(k.getValue());
                                                    detail.setSlMoney(k.getValue());
                                                    break;
                                                case OcrKey.INVOICE_CHECK_CODE://校验码
                                                    detail.setInvoiceCheckCode(k.getValue());
                                                    break;
                                                case OcrKey.COMNAME://购方名称
                                                    detail.setComName(k.getValue());
                                                    break;
                                                case OcrKey.HW://货物或应税劳务、服务名称
                                                    list1.add(k.getValue());
                                                    break;
                                                case OcrKey.GLXH://规格型号
                                                    list2.add(k.getValue());
                                                    break;
                                                case OcrKey.UNIT://单位
                                                    list3.add(k.getValue());
                                                    break;
                                                case OcrKey.SUM://数量
                                                    list4.add(k.getValue());
                                                    break;
                                                case OcrKey.PRICE://单价
                                                    list5.add(k.getValue());
                                                    break;
                                                case OcrKey.OCR_GUOLU_JINES: // 金额
                                                    list6.add(k.getValue());
                                                    break;
                                                case OcrKey.OCR_SL://税率
                                                    detail.setSl(k.getValue());
                                                    list7.add(k.getValue());
                                                    break;
                                                case OcrKey.OCR_SE:
                                                    list8.add(k.getValue());
                                                    break;
                                                case OcrKey.XSFNAME://销售方名称
                                                    detail.setXsComName(k.getValue());
                                                    break;
                                                case OcrKey.XSFNUM://销售方识别号
                                                    detail.setXsfNum(k.getValue());
                                                    break;
                                                case OcrKey.XSFPHONE://销售方地址、电话
                                                    //分割  北京市朝阳区创远路34号院7号楼08层801室010-57077053
                                                    detail.setXsfPhone(k.getValue());
                                                    break;
                                                case OcrKey.XSFNUMBER://销售方开户行及账号
                                                    //分割  招商银行北京北苑路支行110913772110601
                                                    detail.setXsfBankName(PatternUtil.getNum(k.getValue()));
                                                    detail.setXsfNumber(PatternUtil.getStr(k.getValue()));
                                                    break;
                                                case OcrKey.MACHINE_ID: //机器编号
                                                    detail.setMachineId(k.getValue());
                                                    break;
                                                case OcrKey.COMHOME: //购买方地址、电话
                                                    detail.setComHome(k.getValue());
                                                    break;
                                                case OcrKey.COMBANK: //购买方开户行及账号
                                                    detail.setComBank(k.getValue());
                                                    break;
                                                case OcrKey.RECHECK: //复核人
                                                    detail.setRecheck(k.getValue());
                                                    break;
                                                case OcrKey.DRAWER: //开票人
                                                    detail.setDrawer(k.getValue());
                                                    break;
                                                case OcrKey.PAYEE: //收款人
                                                    detail.setPayee(k.getValue());
                                                    break;
                                                case OcrKey.COMMENT: //备注
                                                    detail.setComment(k.getValue());
                                                    break;
                                                case OcrKey.OCR_GUOLU_JINE: //小写金额
                                                    detail.setXxje(k.getValue());
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    });
                                    for (int i = 0; i < list1.size(); i++) {
                                        TentcentOCRInfoVO vo = new TentcentOCRInfoVO();
                                        vo.setProductName(list1.get(i));
                                        vo.setCgGgxh("");
                                        vo.setCgUnit("");
                                        vo.setCnCgum("");
                                        vo.setCgPrice(BigDecimal.valueOf(0));
                                        vo.setCgMoney(BigDecimal.valueOf(0));
                                        vo.setCgGglv("");
                                        vo.setCgGgSE("");
                                        vo.setInvoiceCode(detail.getInvoiceCode());
                                        vo.setInvoiceNum(detail.getInvoiceNum());
                                        vo.setStayDate(NewDateUtil.strToDate(detail.getTravelDate()));
                                        String bxmoney = detail.getBxMoney().replaceAll("¥", "");
                                        vo.setSbMoney(BigDecimal.valueOf(Double.valueOf(bxmoney)));
                                        lissst1.add(vo);
                                    }
                                    if (list2.size() == list1.size()) {
                                        for (int i = 0; i < lissst1.size(); i++) {
                                            lissst1.get(i).setCgGgxh(list2.get(i));
                                        }
                                    }
                                    if (list3.size() == list1.size()) {
                                        for (int i = 0; i < lissst1.size(); i++) {
                                            lissst1.get(i).setCgUnit(list3.get(i));
                                        }
                                    }

                                    for (int i = 0; i < list1.size(); i++) {
                                        lissst1.get(i).setCnCgum(list4.get(i));
                                    }

                                    if (list5.size() == list1.size()) {
                                        for (int i = 0; i < lissst1.size(); i++) {
                                            if (list5.get(i).equals("")) {
                                                lissst1.get(i).setCgPrice(BigDecimal.valueOf(Double.valueOf(0)));
                                            } else {
                                                lissst1.get(i).setCgPrice(BigDecimal.valueOf(Double.valueOf(list5.get(i))));
                                            }
                                        }
                                    }

                                    for (int i = 0; i < lissst1.size(); i++) {
                                        lissst1.get(i).setCgMoney(BigDecimal.valueOf(Double.valueOf(list6.get(i))));
                                    }

                                    if (list7.size() == list1.size()) {
                                        for (int i = 0; i < lissst1.size(); i++) {
                                            lissst1.get(i).setCgGglv(list7.get(i));
                                        }
                                    }

                                    if (list8.size() == list1.size()) {
                                        for (int i = 0; i < lissst1.size(); i++) {
                                            lissst1.get(i).setCgGgSE(list8.get(i));
                                        }
                                    }
                                    detail.setTableData(lissst1);
                                    list.add(detail);
                                });
                                return list;
                            });
                })
                // 识别条数-1
                .flatMap(vo -> {

                    return sysUsageRepository.findByServerName("票据识别")
                            .flatMap(item -> {
                                int sum = Integer.valueOf(item.getRemainingTimes()) - 1;
                                return sysUsageRepository.save(new SysUsage().setId(item.getId()).setRate(item.getRate()).setRemainingTimes(String.valueOf(sum)).setServerName(item.getServerName()))
                                        .map(vo2 -> vo);
                            });

                })
                .flatMap(vo -> {
                    Mono mono1 = ocrService.ftpFileSave(vo);
                    Mono mono2 = ocrService.invoiceRecordSave(vo);
                    Mono<ArrayList<TentcentOCRVO>> map = mono1.flatMap(item -> mono2);
                    return map;
                })
                // 查询账套表发票代码、号码 是否重复
                .flatMap(vo -> {

                    return findByInvoiceCodeAndInvoiceNum(vo.get(0).getInvoiceCode(), vo.get(0).getInvoiceNum())
                            .flatMap(s -> {
                                if (s == 0) {
                                    return Mono.just(R.ok().setCode(200L).setResult(vo));
                                } else {
                                    return Mono.just(R.ok().setCode(111L).setResult("发票重复"));
                                }
                            });
                })
                .map(o -> R.ok().setResult(o));
    }

    /**
     * 查询账套表发票代码、号码 是否重复,并且 账套中增加一条识别记录
     *
     * @return
     */
    public Mono<Long> findByInvoiceCodeAndInvoiceNum(String code, String number) {
        return ocrService.findByInvoiceCodeAndInvoiceNum(code, number);
    }


    @PostMapping("/isCheckApi")
    public Mono<R> isCheckApi(String invoiceCode,String invoiceNum,String invoiceCheckCode,String invoiceDate,String invoiceMoney) {
        return sysCheckApiRepository.findAll().collectList()
                // 获取配置 验真接口信息
                .map(item->{
                    SysCheckApi checkApi=new SysCheckApi();
                    checkApi.setUrl(item.get(0).getUrl()).setKey(item.get(0).getKey()).setSecret(item.get(0).getSecret());
                    return checkApi;
                })
                // 验真--开始
                .map(chenkApi->{
                    OkHttpClient client = new OkHttpClient();
                    MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
                    builder.addFormDataPart("key", chenkApi.getKey());
                    builder.addFormDataPart("secret", chenkApi.getSecret());
                    builder.addFormDataPart("invoiceCode", invoiceCode);
                    builder.addFormDataPart("invoiceNumber", invoiceNum);
                    builder.addFormDataPart("billingDate", invoiceDate.substring(0,10));
                    builder.addFormDataPart("totalAmount", invoiceMoney);
                    builder.addFormDataPart("checkCode", StringUtils.isNotBlank(invoiceCheckCode)?invoiceCheckCode.substring(invoiceCheckCode.length() - 6):"");
                    builder.addFormDataPart("typeId", "3007");
                    builder.addFormDataPart("format", "json");

                    com.squareup.okhttp.RequestBody requestBody = builder.build();
                    Request request = new Request.Builder()
                            .url(chenkApi.getUrl())
                            .post(requestBody)
                            .build();

                    //验真请求
                    Response response = null;
                    String StringTemp="";
                    try {
                        response = client.newCall(request).execute();
                        StringTemp = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //解析返回的json数据
                    JSONObject object = JSONObject.parseObject(StringTemp);
                    JSONObject message = object.getJSONObject("message");
                    String status = message.getString("status");
                    String value = message.getString("value");


                    return CollectOfUtils.mapof("invoiceCode",invoiceCode,"invoiceNumber",invoiceNum,"invoiceDate",invoiceDate,"invoiceMoney",invoiceMoney,"invoiceCheckCode",invoiceCheckCode,"status",status,"message",value);
                })
                // 增加一条验真记录
                .flatMap(map->{
                    InvoiceCheckRecord invoiceCheckRecord=new InvoiceCheckRecord();
                    invoiceCheckRecord
                            .setInvoiceCode(map.get("invoiceCode"))
                            .setInvoiceNum(map.get("invoiceNumber"))
                            .setInvoiceTime(map.get("invoiceDate"))
                            .setInvoiceMoney(map.get("invoiceMoney"))
                            .setInvoiceCheckCode(map.get("invoiceCheckCode"))
                            .setUserSingleNum("test001")
                            .setFlag(map.get("status")+"-"+map.get("message"))
                            .setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    return ocrService.InvoiceCheckRecordSave(invoiceCheckRecord).zipWith(ocrService.InvoiceCheckUsage()).map(m->map.get("status"));
                }).map(a->R.ok().setResult(a));
    }



    @GetMapping("/download")
    public Mono<Void> downloadByWriteWith(ServerHttpResponse response) throws IOException {

        String strFilePath = "D:/awe.xlsx";
        File file = new File(strFilePath);
        ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;
        try {
            response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=" + new String("awe.xlsx".getBytes("UTF-8"), "iso-8859-1"));//输出文件名乱码问题处理
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return zeroCopyResponse.writeWith(file, 0, file.length());
    }


}
