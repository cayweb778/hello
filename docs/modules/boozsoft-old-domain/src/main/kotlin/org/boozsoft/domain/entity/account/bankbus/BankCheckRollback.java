package org.boozsoft.domain.entity.account.bankbus;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：银行支票回滚表", description = "银行支票回滚表")
@Table("bank_check_rollback")
@Data
public class BankCheckRollback {
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "二维码", hidden = true)
    private String qrCode;
    @ApiModelProperty(value = "出票日期", hidden = true)
    private String checkDate;
    @ApiModelProperty(value = "支票类型（现金、转账、旅行、划线、银行、其他）", hidden = true)
    private String checkType;
    @ApiModelProperty(value = "支票编号", hidden = true)
    private String checkNum;
    @ApiModelProperty(value = "支票银行", hidden = true)
    private String checkName;
    @ApiModelProperty(value = "付款行名称", hidden = true)
    private String payName;
    @ApiModelProperty(value = "出票人账号", hidden = true)
    private String payAccount;
    @ApiModelProperty(value = "收款人全称", hidden = true)
    private String payeeName;
    @ApiModelProperty(value = "用途", hidden = true)
    private String purpose;
    @ApiModelProperty(value = "金额", hidden = true)
    private String amount;
    @ApiModelProperty(value = "密押代码", hidden = true)
    private String encryCode;
    @ApiModelProperty(value = "对应凭证号（唯一码）", hidden = true)
    private String voucherNumber;
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
    @ApiModelProperty(value = "变动日期", hidden = true)
    private String biandongDate;
    @ApiModelProperty(value = "变动方式(1.修改;2.删除;3.停用)", hidden = true)
    private String biandongMethod;
    @ApiModelProperty(value = "操作员姓名", hidden = true)
    private String biandongName;
    @ApiModelProperty(value = "操作员唯一标识", hidden = true)
    private String biandongUniqueCode;
    @ApiModelProperty(value = "变动id", hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private String biandongId;

}
