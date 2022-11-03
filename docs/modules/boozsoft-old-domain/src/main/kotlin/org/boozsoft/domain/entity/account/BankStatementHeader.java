package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("bank_statement_header")
@ApiModel(value = "数据表：银行对账期初表头", description = "银行对账期初表头")
public class BankStatementHeader {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "年度", hidden = true)
    private String iyear;
    @ApiModelProperty(value = "启用日期", hidden = true)
    private String qiyongDate;
    @ApiModelProperty(value = "对应的银行科目", hidden = true)
    private String ccode;
    @ApiModelProperty(value = "是否记账(1.记账)", hidden = true)
    private String flag;
    @ApiModelProperty(value = "单位日记账调整前余额", hidden = true)
    private String dayBeforeMoney;
    @ApiModelProperty(value = "银行对账单调整前余额", hidden = true)
    private String statementBeforeMoney;
    @ApiModelProperty(value = "单位日记账调整后余额", hidden = true)
    private String dayEndMoney;
    @ApiModelProperty(value = "银行对账单调整后余额", hidden = true)
    private String statementEndMoney;
    @ApiModelProperty(value = "方向（1：借，0：贷）", hidden = true)
    private String fangxiang;
    @ApiModelProperty(value = "币种", hidden = true)
    private String currencyId;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;
    @ApiModelProperty(value ="", hidden = true)
    private String beiyong4;
    @ApiModelProperty(value ="", hidden = true)
    private String beiyong5;
}
