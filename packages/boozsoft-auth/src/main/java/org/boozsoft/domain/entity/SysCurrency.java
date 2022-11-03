package org.boozsoft.domain.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：币种信息", description = "币种信息")
@Table("_app_group_sys_currency")
@Data
public class SysCurrency {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    private String id;
    @ApiModelProperty(value = "唯一标识", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "国家名称", hidden = true)
    private String countryName;
    @ApiModelProperty(value = "中文名称", hidden = true)
    private String currencyZhCnName;
    @ApiModelProperty(value = "英文名称", hidden = true)
    private String currencyEnName;
    @ApiModelProperty(value = "外币符号", hidden = true)
    private String currencySymbol;
    @ApiModelProperty(value = "国际代码", hidden = true)
    private String abbreviation;
    @ApiModelProperty(value = "换算率", hidden = true)
    private String fractionalCurrency;
    @ApiModelProperty(value = "币种单位", hidden = true)
    private String currencyUnit;
    @ApiModelProperty(value = "比特币", hidden = true)
    private String currencyBtc;
    private Integer num;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong4;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong5;

    @Transient
    private String label;

    @Transient
    private String value;

}
