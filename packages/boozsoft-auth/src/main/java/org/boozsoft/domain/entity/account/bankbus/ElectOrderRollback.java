package org.boozsoft.domain.entity.account.bankbus;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：电子回单回滚表", description = "电子回单回滚表")
@Table("elect_order_rollback")
@Data
public class ElectOrderRollback {
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "回单日期", hidden = true)
    private String electOderDate;
    @ApiModelProperty(value = "回单类型（付款、收款、费用、税款、社保、其他）", hidden = true)
    private String electOderType;
    @ApiModelProperty(value = "回单样式（电子、纸质）", hidden = true)
    private String electOderStyle;
    @ApiModelProperty(value = "交易类型（收入或支出）", hidden = true)
    private String electOderTrans;
    @ApiModelProperty(value = "电子回单分类", hidden = true)
    private String electClass;
    @ApiModelProperty(value = "交易流水号", hidden = true)
    private String serialNumber;
    @ApiModelProperty(value = "币种", hidden = true)
    private String currency ;
    @ApiModelProperty(value = "金额", hidden = true)
    private String amount;
    @ApiModelProperty(value = "手续费", hidden = true)
    private String commission;
    @ApiModelProperty(value = "凭证号", hidden = true)
    private String transId;
    @ApiModelProperty(value = "付款人全称", hidden = true)
    private String payName ;
    @ApiModelProperty(value = "付款人账号", hidden = true)
    private String payAccount;
    @ApiModelProperty(value = "付款人开户行", hidden = true)
    private String payBank;
    @ApiModelProperty(value = "收款人全称", hidden = true)
    private String payeeName;
    @ApiModelProperty(value = "收款人账号", hidden = true)
    private String payeeAccount;
    @ApiModelProperty(value = "收款人开户行", hidden = true)
    private String payeeBank;
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
