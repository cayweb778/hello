package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.Accvoucher;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 视图实体类
 *
 * @author lz
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@ApiModel(value = "总账列表对象", description = "总账列表对象")
public class GeneralLedgerVo {

    @ApiModelProperty("序号")
    private Integer number;
    @ApiModelProperty("期间")
    private  String num;
    @ApiModelProperty("摘要")
    private  String name;
    @ApiModelProperty("方向")
    private  String fx;
    @ApiModelProperty("计量单位")
    private  String dw;
    @ApiModelProperty("外币名称")
    private  String wb;

    private  String ccode;

    private String ccodeName;

    //前段展示金额格式化
    private  BigDecimal sjnum;
    private  BigDecimal  sjwb;
    private  BigDecimal sjmoney;

    private  BigDecimal sdnum;
    private  BigDecimal sdwb;
    private  BigDecimal sdmoney;

    private  BigDecimal synum;
    private  BigDecimal sywb;
    private  BigDecimal symoney;

    // 期初累计 用于本年累计 计算
    private  BigDecimal ljmd;
    private  BigDecimal ljmc;
    private  BigDecimal ljwbmd;
    private  BigDecimal ljwbmc;
    private  BigDecimal ljslmd;
    private  BigDecimal ljslmc;


}
