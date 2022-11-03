package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 视图实体类
 *
 * @author lz
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@ApiModel(value = "科目多栏账列表对象", description = "科目多栏账列表对象")
public class GeneralLedgerMultiVo {

    @ApiModelProperty("序号")
    private Integer number;

    @ApiModelProperty("日期")
    private  String dbillDate;

    @ApiModelProperty("凭证字号")
    private  String inoId;

    @ApiModelProperty("摘要")
    private  String cdigest;

    @ApiModelProperty("外币名称")
    private  String wb;

    @ApiModelProperty("方向")
    private  String fx;

    //借方
    private  BigDecimal sjmoney;

    //贷方
    private  BigDecimal sdmoney;

    //余额
    private  BigDecimal symoney;

    private List<Entry> list;

    //科目信息list
    @Data
    public static class Entry {
        private  String key;
        private  String name;
        private  String fx;
        private  String money;

        private  BigDecimal num;
        private  BigDecimal wb;
    }

}
