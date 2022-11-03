package org.boozsoft.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "汇率分录", description = "汇率分录")
@Table("exchange_rate_entry_rollback")
@Data
public class ExchangeRateEntryRollback {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "主表唯一编码", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "会计期间（年月or年月日）", hidden = true)
    private String iperiod;
    @ApiModelProperty(value = "记账汇率（实数）", hidden = true)
    private String rateBook;
    @ApiModelProperty(value = "调整汇率（实数）", hidden = true)
    private String rateJust;
    @ApiModelProperty(value = "登记时间", hidden = true)
    private String checkInTime;
    @ApiModelProperty(value = "对应外币编码", hidden = true)
    private String currencyCode;
    @ApiModelProperty(value = "",hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;
    @ApiModelProperty(value = "变动日期", position = 12)
    private String biandongDate;
    @ApiModelProperty(value = "变动ID", position = 12)
    private String biandongId;
    @ApiModelProperty(value = "变动方式(1.修改;2.删除;3.停用)", position = 13)
    private String biandongMethod;

    @ApiModelProperty(value = "操作员姓名", position = 14)
    private String biandongName;

    @ApiModelProperty(value = "操作员唯一编码", position = 15)
    private String biandongUniqueCode;

    @ApiModelProperty(value = "供应商分类级次编码（默认3位）", position = 16)
    private String supGradeCode;
}
