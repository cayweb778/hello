package org.boozsoft.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "汇率分录", description = "汇率分录")
@Table("exchange_rate_entry")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateEntry {

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

    public ExchangeRateEntry(String uniqueCode, String iperiod, String rateBook, String checkInTime, String currencyCode) {
        this.uniqueCode = uniqueCode;
        this.iperiod = iperiod;
        this.rateBook = rateBook;
        this.checkInTime = checkInTime;
        this.currencyCode = currencyCode;
    }
}
