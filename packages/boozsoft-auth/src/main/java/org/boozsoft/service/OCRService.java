package org.boozsoft.service;

import org.boozsoft.domain.entity.FtpFile;
import org.boozsoft.domain.entity.invoice.InvoiceCheckRecord;
import org.boozsoft.domain.vo.TentcentOCRVO;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

/**
 * @ClassName : OCRService
 * @Description :
 * @Author : miao
 * @Date: 2020-10-17 15:55
 */
public interface OCRService {
    Mono<Long> findByInvoiceCodeAndInvoiceNum(String a, String b);
    Mono<FtpFile> ftpFileSave(ArrayList<TentcentOCRVO> list);
    Mono<ArrayList<TentcentOCRVO>> invoiceRecordSave(ArrayList<TentcentOCRVO> list);
    Mono InvoiceCheckUsage();
    Mono InvoiceCheckRecordSave(InvoiceCheckRecord invoiceCheckRecord);
}
