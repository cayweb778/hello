package org.boozsoft.domain.entity.account.bankbus;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：银行汇票回滚表", description = "银行汇票回滚表")
@Table("bank_bill_rollback")
@Data
public class BankBillRollback {
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "二维码", hidden = true)
    private String qrCode;
    @ApiModelProperty(value = "出票日期", hidden = true)
    private String billDate;
    @ApiModelProperty(value = "汇票类型（银行承兑、商业承兑）", hidden = true)
    private String billType;
    @ApiModelProperty(value = "汇票编号", hidden = true)
    private String billNum;
    @ApiModelProperty(value = "出票人全称", hidden = true)
    private String billName;
    @ApiModelProperty(value = "银行汇票分类", hidden = true)
    private String billClass;
    @ApiModelProperty(value = "出票人账号", hidden = true)
    private String billAccount;
    @ApiModelProperty(value = "付款行全称", hidden = true)
    private String bankName;
    @ApiModelProperty(value = "出票金额", hidden = true)
    private String amount;
    @ApiModelProperty(value = "汇票到期日", hidden = true)
    private String dueDate;
    @ApiModelProperty(value = "承兑协议编号", hidden = true)
    private String transId;
    @ApiModelProperty(value = "付款行行号", hidden = true)
    private String payBankCode;
    @ApiModelProperty(value = "付款人地址", hidden = true)
    private String payAddr;
    @ApiModelProperty(value = "付款人开户行", hidden = true)
    private String payBank;
    @ApiModelProperty(value = "收款人全称", hidden = true)
    private String payeeName;
    @ApiModelProperty(value = "收款人账号", hidden = true)
    private String payeeAccount;
    @ApiModelProperty(value = "收款人开户行", hidden = true)
    private String payeeBank;
    @ApiModelProperty(value = "密押代码", hidden = true)
    private String encryCode;
    @ApiModelProperty(value = "附言摘要", hidden = true)
    private String postscriptSummary;
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
