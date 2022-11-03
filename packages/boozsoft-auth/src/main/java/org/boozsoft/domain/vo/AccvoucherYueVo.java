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
public class AccvoucherYueVo {
    private String ccode;//科目编码
    private String ccodeName;//科目名称
    private String unitMeasurement;//计量单位
    private String foreignCurrency;//外币名称
    private String bprogerty;//科目方向
    private BigDecimal md;//借方金额
    private BigDecimal mc;//贷方金额
    private BigDecimal nfratMd;//原币借方金额
    private BigDecimal nfratMc;//原币贷方金额
    private BigDecimal ndS;//借方数量
    private BigDecimal ncS;//贷方数量
    private String bbank;
    private String bcash;
}
