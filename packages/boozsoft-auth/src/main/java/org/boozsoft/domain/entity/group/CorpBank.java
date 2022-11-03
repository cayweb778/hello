package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：账套账户表", description = "账套账户表")
@Table("corp_bank")
@Data
public class CorpBank {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "账户编码", hidden = true)
    private String corpBankCode;
    @ApiModelProperty(value = "账户名称", hidden = true)
    private String corpBankName;
    @ApiModelProperty(value = "账户类型（现金、银行、支付宝、微信、其他）", hidden = true)
    private String corpBankType;
    @ApiModelProperty(value = "银行编码（不允许重复）", hidden = true)
    private String bankCode;
    @ApiModelProperty(value = "对应会计科目", hidden = true)
    private String ccode;
    @ApiModelProperty(value = "第三方账号", hidden = true)
    private String accountNum;
    @ApiModelProperty(value = "是否现金（1是，0否）", hidden = true)
    private String icash;
    @ApiModelProperty(value = "是否银行（1是，0否）", hidden = true)
    private String ibank;
    @ApiModelProperty(value = "是否银行对账（1是，0否）", hidden = true)
    private String ibankStatement;
    @ApiModelProperty(value = "开户机构", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "开户地", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "银行账户", hidden = true)
    private String beiyong3;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong4;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong5;

}
