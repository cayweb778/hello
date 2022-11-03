package org.boozsoft.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * ocr识别字段
 * Created by CodeGenerator on 2020/09/04.
 */
@Data
public class TentcentOCRInfoVO {
    private String invoiceCode;            // 发票号码
    private String invoiceNum;            // 发票代码
    private Date stayDate;                // 开票日期
    private BigDecimal sbMoney;           // 价税合计
    @JsonProperty("stockName")
    private String productName;           // 产品名称
    @JsonProperty("unit")
    private String cgUnit;                // 单位
    @JsonProperty("stockNum")
    private String cnCgum;                // 数量
    @JsonProperty("stockModel")
    private String cgGgxh;                // 规格型号
    @JsonProperty("taxRate")
    private String cgGglv;                // 税率
    @JsonProperty("taxes")
    private String cgGgSE;                // 税额
    @JsonProperty("price")
    private BigDecimal cgPrice;           // 单价
    @JsonProperty("amount")
    private BigDecimal cgMoney;           // 预计金额
    private String invFlgs;               // 1 已验真 2假
}