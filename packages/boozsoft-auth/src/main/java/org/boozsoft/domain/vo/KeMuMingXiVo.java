package org.boozsoft.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class KeMuMingXiVo {
	private Integer number;// 序号
	private String iyperiod;//
	private String ccode;//
	private String ccodeName;//
	private String dbillDate;// 凭证日期
	private Integer inoIdASC;// 凭证号
    private String inoId;// 凭证号= 凭证类型+凭证号
    private String cdigest;// 凭证摘要
    private BigDecimal md;// 借方
    private BigDecimal mc;// 贷方
    private BigDecimal ljmd;// 累计借方
    private BigDecimal ljmc;// 累计贷方
    private BigDecimal yue;// 期初余额
    private String bprogerty;// 方向
    private BigDecimal ndS;// 借方数量
    private BigDecimal ncS;// 贷方数量
    private BigDecimal ncnum;// 数量
    private BigDecimal yue_num;// 余额-数量
    private BigDecimal cunitPrice;// 单价
    private String unitMeasurement;// 计量单位
    private BigDecimal mdF;// 外币汇率
    private BigDecimal nfrat_md;// 外币金额-借
    private BigDecimal nfrat_mc;// 外币金额-贷
    private BigDecimal yue_nfrat;// 外币金额-余额
    private String foreignCurrency;// 币种名称
    private String pjCsettle;// 结算方式
    private String pjId;// 票据号
}
