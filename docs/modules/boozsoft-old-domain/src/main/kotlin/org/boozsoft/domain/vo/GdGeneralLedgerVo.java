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
public class GdGeneralLedgerVo {

    @ApiModelProperty("序号")
    private Integer number;

    @ApiModelProperty("日期")
    private  String rq;

    @ApiModelProperty("类别编码")
    private  String lbCode;
    @ApiModelProperty("类别")
    private  String lb;
    @ApiModelProperty("项目")
    private  String xm;
    @ApiModelProperty("部门")
    private  String bm;

    @ApiModelProperty("借方")
    private  String yjf;
    @ApiModelProperty("贷方")
    private  String ydf;
    @ApiModelProperty("余额")
    private  String yye;

    @ApiModelProperty("借方")
    private  String zjf;
    @ApiModelProperty("贷方")
    private  String zdf;
    @ApiModelProperty("余额")
    private  String zye;

    @ApiModelProperty("借方")
    private  String jjf;
    @ApiModelProperty("贷方")
    private  String jdf;
    @ApiModelProperty("余额")
    private  String jye;

    @ApiModelProperty("净值")
    private  String jingzhi;

}
