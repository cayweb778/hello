package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 视图实体类
 *
 * @author lz
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@ApiModel(value = "总账列表对象", description = "总账列表对象")
public class DeptGeneralLedgerVo {

    @ApiModelProperty("序号")
    private Integer number;
    @ApiModelProperty("科目编码")
    private  String ccode;
    @ApiModelProperty("科目名称")
    private  String cname;
    @ApiModelProperty("计量单位")
    private  String dw;
    @ApiModelProperty("外币名称")
    private  String wb;

    //期初  
    private  BigDecimal qcJfnum;
    private  BigDecimal qcJfwb;
    private  BigDecimal qcJfMoney;
    private  BigDecimal qcDfnum;
    private  BigDecimal qcDfwb;
    private  BigDecimal qcDfmoney;
    //发生
    private  BigDecimal jfnum;
    private  BigDecimal jfwb;
    private  BigDecimal jfMoney;
    private  BigDecimal dfnum;
    private  BigDecimal dfwb;
    private  BigDecimal dfmoney;
    //累计
    private  BigDecimal ljJfnum;
    private  BigDecimal ljJfwb;
    private  BigDecimal ljJfMoney;
    private  BigDecimal ljDfnum;
    private  BigDecimal ljDfwb;
    private  BigDecimal ljDfmoney;
    //期末
    private  BigDecimal qmJfnum;
    private  BigDecimal qmJfwb;
    private  BigDecimal qmJfMoney;
    private  BigDecimal qmDfnum;
    private  BigDecimal qmDfwb;
    private  BigDecimal qmDfmoney;

    @ApiModelProperty(value = "部门编码（不允许重复）")
    private String dcode;
    @ApiModelProperty(value = "部门名称")
    private String dname;

    //期初累计 用于本年累计 计算
    private  BigDecimal ljmd;
    private  BigDecimal ljmc;
    private  BigDecimal ljwbmd;
    private  BigDecimal ljwbmc;
    private  BigDecimal ljslmd;
    private  BigDecimal ljslmc;
}
