package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：人员表", description = "人员表")
@Table("sys_psn_rollback")
@Data
public class SysPsnRollback {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "唯一标识", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "员工编码（不允许重复）", hidden = true)
    private String psnCode;
    @ApiModelProperty(value = "工号", hidden = true)
    private String jobNum;
    @ApiModelProperty(value = "员工姓名", hidden = true)
    private String psnName;
    @ApiModelProperty(value = "员工性别（0.未知的性别；1.男性；2.女性；9.未说明的性别）", hidden = true)
    private String psnSex;
    @ApiModelProperty(value = "部门唯一编码", hidden = true)
    private String uniqueCodeDept;
    @ApiModelProperty(value = "人员所属类别唯一编码", hidden = true)
    private String uniquePsnType;
    @ApiModelProperty(value = "员工属性（内部或外部，默认内部员工）", hidden = true)
    private String psnType;
    @ApiModelProperty(value = "员工职务", hidden = true)
    private String psnPost;
    @ApiModelProperty(value = "证件类型", hidden = true)
    private String documentType;
    @ApiModelProperty(value = "证件号码", hidden = true)
    private String documentCode;
    @ApiModelProperty(value = "工位", hidden = true)
    private String psnStation;
    @ApiModelProperty(value = "手机号", hidden = true)
    private String cellPhoneNum;
    @ApiModelProperty(value = "email", hidden = true)
    private String psnEmail;
    @ApiModelProperty(value = "微信号", hidden = true)
    private String psnWechat;
    @ApiModelProperty(value = "QQ", hidden = true)
    private String psnQq;
    @ApiModelProperty(value = "通信地址", hidden = true)
    private String psnAddress;
    @ApiModelProperty(value = "开户银行", hidden = true)
    private String psnBank;
    @ApiModelProperty(value = "开户地", hidden = true)
    private String bankArea;
    @ApiModelProperty(value = "银行账号", hidden = true)
    private String bankAccount;
    @ApiModelProperty(value = "入职日期", hidden = true)
    private String entryDate;
    @ApiModelProperty(value = "创建日期", hidden = true)
    private String createDate;
    @ApiModelProperty(value = "是否停用(1.启用;0停用)", hidden = true)
    private String flag;
    @ApiModelProperty(value = "所属国家编码", hidden = true)
    private String countryId;
    @ApiModelProperty(value = "行政区划唯一编码", hidden = true)
    private String uniqueCodeZone;
    @ApiModelProperty(value = "银行行号", hidden = true)
    private String bankNum;
    @ApiModelProperty(value = "省", hidden = true)
    private String province;
    @ApiModelProperty(value = "市", hidden = true)
    private String city;
    @ApiModelProperty(value = "区", hidden = true)
    private String district;
    @ApiModelProperty(value = "出生日期", hidden = true)
    private String birthDate;
    @ApiModelProperty(value = "离职日期", hidden = true)
    private String leaveDate;
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
