package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：银行档案回滚表", description = "银行档案回滚表")
@Table("bank_rollback")
@Data
public class BankRollback {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "银行编码（不允许重复）", hidden = true)
    private String bankCode;
    @ApiModelProperty(value = "银行名称", hidden = true)
    private String bankName;
    @ApiModelProperty(value = "银行账号", hidden = true)
    private String bankAccount;
    @ApiModelProperty(value = "开户地", hidden = true)
    private String bankAddr;
    @ApiModelProperty(value = "开户地", hidden = true)
    private String bankArea;
    @ApiModelProperty(value = "行号", hidden = true)
    private String bankNum;
    @ApiModelProperty(value = "联系人", hidden = true)
    private String lianxiPerson;
    @ApiModelProperty(value = "联系电话", hidden = true)
    private String lianxiPhone;
    @ApiModelProperty(value = "联系人QQ", hidden = true)
    private String lianxiQq;
    @ApiModelProperty(value = "联系人微信", hidden = true)
    private String lianxiWechat;
    @ApiModelProperty(value = "国家", hidden = true)
    private String countryId;
    @ApiModelProperty(value = "行政区划", hidden = true)
    private String zoneId;
    @ApiModelProperty(value = "详细地址", hidden = true)
    private String address;
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
    @ApiModelProperty(value = "生效状态（1是；0否）", hidden = true)
    private String successState;
    @ApiModelProperty(value = "申请账套唯一码", hidden = true)
    private String applyDatabaseUniqueCode;
    @ApiModelProperty(value = "申请人", hidden = true)
    private String applyUser;
    @ApiModelProperty(value = "申请时间", hidden = true)
    private String applyDate;
    @ApiModelProperty(value = "审批人", hidden = true)
    private String approveUser;
    @ApiModelProperty(value = "审批时间", hidden = true)
    private String approveDate;
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
