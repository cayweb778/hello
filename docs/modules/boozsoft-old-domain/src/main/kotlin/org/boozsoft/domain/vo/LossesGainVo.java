package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;


@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class LossesGainVo {
    private String lossCcode;           // 损益科目
    private String lossCcodeName;
    private String lossBprogerty;
    private String lossBend;
    private String profitCcode;         // 利润科目
    private String profitBprogerty;         // 利润科目方向
    private String digest;         // 凭证摘要
    private BigDecimal qmMd;
    private BigDecimal qmMc;
    @ApiModelProperty(value = "凭证数量")
    private BigDecimal qmNum;
    @ApiModelProperty(value = "凭证原币金额")
    private BigDecimal qmNfrat;

    private String childrenflag;   // 表示 1是下级科目辅助余额、0无
    private String fuzhuName;   // 辅助项

    private String cdeptId;
    private String cpersonId;
    private String ccusId;
    private String csupId;
    private String projectId;

    private String cdfine1;
    private String cdfine2;
    private String cdfine3;
    private String cdfine4;
    private String cdfine5;
    private String cdfine6;
    private String cdfine7;
    private String cdfine8;
    private String cdfine9;
    private String cdfine10;
    private String cdfine11;
    private String cdfine12;
    private String cdfine13;
    private String cdfine14;
    private String cdfine15;
    private String cdfine16;
    private String cdfine17;
    private String cdfine18;
    private String cdfine19;
    private String cdfine20;
    private String cdfine21;
    private String cdfine22;
    private String cdfine23;
    private String cdfine24;
    private String cdfine25;
    private String cdfine26;
    private String cdfine27;
    private String cdfine28;
    private String cdfine29;
    private String cdfine30;

    private List<QjsyjzVo> yuemx;// 主要是 下级科目余额
    private List<QjsyjzVo> children;// 主要是 下级科目辅助余额
    private List<LossesGainVo> children2;// 查询一级时使用
}
