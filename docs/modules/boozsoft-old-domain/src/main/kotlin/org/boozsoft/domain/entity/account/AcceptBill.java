package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@ApiModel(value = "数据表：承兑汇票表", description = "承兑汇票表")
@Table("accept_bill")
@Data
public class AcceptBill {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "票据编号", hidden = true)
    private String billCode;
    @ApiModelProperty(value = "出票类型（1.银行承兑汇票；2.商业承兑汇票）", hidden = true)
    private String billType;
    @ApiModelProperty(value = "方向（1.收款；2.付款）", hidden = true)
    private String fangxiang;
    @ApiModelProperty(value = "收到日期", hidden = true)
    private String receiveDate;
    @ApiModelProperty(value = "出票单位", hidden = true)
    private String chupiaoUnit;
    @ApiModelProperty(value = "出票单位账号", hidden = true)
    private String chupiaoUnitAccount;
    @ApiModelProperty(value = "出票日期", hidden = true)
    private String chupiaoDate;
    @ApiModelProperty(value = "到期日", hidden = true)
    private String daoqiDate;
    @ApiModelProperty(value = "结算方式", hidden = true)
    private String settModes;
    @ApiModelProperty(value = "付款单位银行", hidden = true)
    private String payBank;
    @ApiModelProperty(value = "收款单位", hidden = true)
    private String paymentUnit;
    @ApiModelProperty(value = "收款单位账号", hidden = true)
    private String paymentAccount;
    @ApiModelProperty(value = "收款单位开户银行", hidden = true)
    private String paymentBank;
    @ApiModelProperty(value = "收款行行号", hidden = true)
    private String paymentBankNum;
    @ApiModelProperty(value = "币种", hidden = true)
    private String currency;
    @ApiModelProperty(value = "汇率", hidden = true)
    private String huilv;
    @ApiModelProperty(value = "金额", hidden = true)
    private String money;
    @ApiModelProperty(value = "交易合同号码", hidden = true)
    private String contractNum;
    @ApiModelProperty(value = "票面利率", hidden = true)
    private String billLilv;
    @ApiModelProperty(value = "付款行行号", hidden = true)
    private String payBankNum;
    @ApiModelProperty(value = "付款行地址", hidden = true)
    private String payBankAddress;
    @ApiModelProperty(value = "背书人", hidden = true)
    private String beishuUser;
    @ApiModelProperty(value = "背书金额", hidden = true)
    private String beishuMoney;
    @ApiModelProperty(value = "备注", hidden = true)
    private String remarks;
    @ApiModelProperty(value = "业务员", hidden = true)
    private String yewuUser;
    @ApiModelProperty(value = "票据摘要", hidden = true)
    private String billRemarks;
    @ApiModelProperty(value = "制单人", hidden = true)
    private String createUser;
    @ApiModelProperty(value = "制单日期", hidden = true)
    @CreatedDate
    private LocalDateTime createDate;
    @ApiModelProperty(value = "修改人", hidden = true)
    private String editUser;
    @ApiModelProperty(value = "修改日期", hidden = true)
    @LastModifiedDate
    private LocalDateTime editDate;
    @ApiModelProperty(value = "状态（0.已到期、0.未到期、1.已贴现、2.已背书、3.已转出、4.已结算、-1贴现锁定、-2背书锁定）", hidden = true)
    private String flag;
    @ApiModelProperty(value = "唯一码", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "图片路径", hidden = true)
    private String imgUrl;
    @ApiModelProperty(value = "承兑日期", hidden = true)
    private String acceptDate;
    @ApiModelProperty(value = "背书日期", hidden = true)
    private String beishuDate;
    @ApiModelProperty(value = "贴现人", hidden = true)
    private String tiexianUser;
    @ApiModelProperty(value = "贴现日期", hidden = true)
    private String tiexianDate;
    @ApiModelProperty(value = "转出人", hidden = true)
    private String outUser;
    @ApiModelProperty(value = "转出日期", hidden = true)
    private String outDate;
    @ApiModelProperty(value = "结算人", hidden = true)
    private String jiesuanUser;
    @ApiModelProperty(value = "结算日期", hidden = true)
    private String jiesuanDate;
    @ApiModelProperty(value = "审核状态（1.已审核）", hidden = true)
    private String icheck;
    @ApiModelProperty(value = "审核时间", hidden = true)
    private String icheckDate;
    @ApiModelProperty(value = "审核人", hidden = true)
    private String icheckUser;
    @ApiModelProperty(value = "是否期初（1.是期初）", hidden = true)
    private String qichu;
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
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong6;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong7;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong8;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong9;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong10;

}
