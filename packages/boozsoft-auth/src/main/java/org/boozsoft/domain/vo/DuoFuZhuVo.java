package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class DuoFuZhuVo {

    private String deptUnique;
    private String deptCode;
    private String deptName;

    private String psnUnique;
    private String psnCode;
    private String psnName;

    private String cusUnique;
    private String cusCode;
    private String cusName;

    private String supUnique;
    private String supCode;
    private String supName;

    private String proUnique;
    private String proCode;
    private String proName;

    /********************* 期初 ************************/
    @ApiModelProperty(value = "借方")
    private BigDecimal qcMd;
    @ApiModelProperty(value = "贷方")
    private BigDecimal qcMc;
    @ApiModelProperty(value = "期初数量")
    private BigDecimal qcNum;
    @ApiModelProperty(value = "期初原币金额")
    private BigDecimal qcNfrat;

    /********************* 凭证 ************************/
    @ApiModelProperty(value = "借方")
    private BigDecimal pzMd;
    @ApiModelProperty(value = "贷方")
    private BigDecimal pzMc;
    @ApiModelProperty(value = "凭证数量")
    private BigDecimal pzNum;
    @ApiModelProperty(value = "凭证原币金额")
    private BigDecimal pzNfrat;
    /********************* 期末余额 ************************/
    @ApiModelProperty(value = "借方")
    private BigDecimal qmMd;
    @ApiModelProperty(value = "贷方")
    private BigDecimal qmMc;
    @ApiModelProperty(value = "凭证数量")
    private BigDecimal qmNum;
    @ApiModelProperty(value = "凭证原币金额")
    private BigDecimal qmNfrat;
}
