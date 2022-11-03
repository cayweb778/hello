package org.boozsoft.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "汇率主表", description = "汇率主表")
@Table("exchange_rate_rollback")
@Data
public class ExchangeRateRollback {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "外币国际代码", hidden = true)
    private String foreignCode;
    @ApiModelProperty(value = "本币国际代码", hidden = true)
    private String localCode;
    @ApiModelProperty(value = "折算方式（外币*汇率=本位币或者外币/汇率=本位币）", hidden = true)
    private String comMethod;
    @ApiModelProperty(value = "取值方式", hidden = true)
    private String valueMethod;
    @ApiModelProperty(value = "汇率小数点位数（默认5位，实数）", hidden = true)
    private String decimalNum;
    @ApiModelProperty(value = "最大误差（默认0.00001）", hidden = true)
    private String errorValue;
    @ApiModelProperty(value = "汇率类型（固定或浮动）", hidden = true)
    private String rateStyle;
    @ApiModelProperty(value = "账套ID")
    private String accountId;
    @ApiModelProperty(value = "会计年度")
    private String iyear;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;

    @ApiModelProperty(value = "变动ID", position = 12)
    private String biandongId;
    @ApiModelProperty(value = "变动日期", position = 12)
    private String biandongDate;

    @ApiModelProperty(value = "变动方式(1.修改;2.删除;3.停用)", position = 13)
    private String biandongMethod;

    @ApiModelProperty(value = "操作员姓名", position = 14)
    private String biandongName;

    @ApiModelProperty(value = "操作员唯一编码", position = 15)
    private String biandongUniqueCode;

    @ApiModelProperty(value = "供应商分类级次编码（默认3位）", position = 16)
    private String supGradeCode;


}
