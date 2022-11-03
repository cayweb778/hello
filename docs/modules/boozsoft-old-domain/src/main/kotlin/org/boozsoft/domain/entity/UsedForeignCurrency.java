package org.boozsoft.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "常用外币", description = "常用外币")
@Table("used_foreign_currency")
@Data
@NoArgsConstructor
public class UsedForeignCurrency {
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "外币国际代码", hidden = true)
    private String foreignCode;
    @ApiModelProperty(value = "外币名称", hidden = true)
    private String foreignName;
    @ApiModelProperty(value = "外币简称", hidden = true)
    private String foreignSimpName;
    @ApiModelProperty(value = "账套ID")
    private String accountId;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;
    @ApiModelProperty(value = "", hidden = true)
    private String tenantId;
    @ApiModelProperty(value = "货币单位", hidden = true)
    private String currencyUnit;
    @ApiModelProperty(value = "比特币", hidden = true)
    private String currencyBtc;
    public UsedForeignCurrency(String foreignCode, String foreignName, String foreignSimpName) {
        this.foreignCode = foreignCode;
        this.foreignName = foreignName;
        this.foreignSimpName = foreignSimpName;
    }
}
