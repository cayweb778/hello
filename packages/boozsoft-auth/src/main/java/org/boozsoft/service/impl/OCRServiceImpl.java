package org.boozsoft.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.FtpFile;
import org.boozsoft.domain.entity.invoice.InvoiceCheckRecord;
import org.boozsoft.domain.entity.invoice.InvoiceOcrRecord;
import org.boozsoft.domain.entity.SysUsage;
import org.boozsoft.domain.vo.TentcentOCRVO;
import org.boozsoft.repo.*;
import org.boozsoft.service.OCRService;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName : JieKuanServiceImpl
 * @Description :
 * @Author : miao
 * @Date: 2020-09-22 13:49
 */
@Slf4j
@Service
public class OCRServiceImpl implements OCRService {

    @Autowired
    SysOcrApiRepository sysOcrApiRepository;
    @Autowired
    FtpFileRepository ftpFileRepository;
    @Autowired
    SysUsageRepository sysUsageRepository;
    @Autowired
    InvoiceHeaderRepository invoiceHeaderRepository;
    @Autowired
    InvoiceOcrRecordRepository invoiceOcrRecordRepository;
    @Autowired
    InvoiceCheckRecordRepository invoiceCheckRecordRepository;




    /* 翔云票据验真 */
//    String key = "3eZpVbN2oTbmpYRtejQwwa";
//    String secret = "6109f9521cf44947bb60d36245250d26";
//    String url = "https://netocr.com/verapi/verInvoice.do";

    //    orc票据识别
//    String secretId = "AKIDEBr1VkllX4GC3etSUQhlnbKwmWAZq0MY";
//    String secretKey = "BLLBoHlMjrKXheO9fxyVlJdeGdkaQ2HI";


    /**
     * 校验 发票代码、号码是否重复
     * @param invoiceCode
     * @param invoiceNum
     * @return
     */
    
    public Mono<Long> findByInvoiceCodeAndInvoiceNum(String invoiceCode,String invoiceNum){
        return invoiceHeaderRepository.countByFapiaoCodeAndFapiaoNumber(invoiceCode,invoiceNum); 
    }

    /**
     * 账套中增加ftp上传文件信息
     * @return
     */
    
    public Mono<FtpFile> ftpFileSave(ArrayList<TentcentOCRVO> list) {
        return ftpFileRepository.save(list.get(0).getFtpFile());
    }

    /**
     * 获取图片ID 并 增加一条OCR识别记录
     * @return
     */
    
    public Mono<ArrayList<TentcentOCRVO>> invoiceRecordSave(ArrayList<TentcentOCRVO> list) {
        return ftpFileRepository.findByNewName(list.get(0).getFtpFile().getNewName())
                .map(ftp->{
                    InvoiceOcrRecord record=new InvoiceOcrRecord();
                    record.setFileId(ftp.getId()).setInvoiceJson(list.get(0).getInvoiceJson()).setType("电子发票").setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).setUserSingleNum("test001");
                    return record;
                })
                .flatMap(invoiceOcrRecordRepository::save).map(o->list);

    }

    /**
     * 增加验真记录
     * @param invoiceCheckRecord
     * @return
     */
    
    public Mono InvoiceCheckRecordSave(InvoiceCheckRecord invoiceCheckRecord) {
        return invoiceCheckRecordRepository.save(invoiceCheckRecord);
    }

    /**
     * 减去一条验真次数
     * @return
     */
    public Mono InvoiceCheckUsage() {
        return sysUsageRepository.findByServerName("票据识别")
                .flatMap(item -> {
                    int sum = Integer.valueOf(item.getRemainingTimes()) - 1;
                    return sysUsageRepository.save(new SysUsage().setId(item.getId()).setRate(item.getRate()).setRemainingTimes(String.valueOf(sum)).setServerName(item.getServerName()))
                            .map(vo2 -> vo2);
                });
    }
}
