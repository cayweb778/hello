//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.boozsoft.domain.entity.group;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.AssertTrue;
import java.io.Serializable;

@Table("_app_group_audit_operator")
@Data
@Accessors(chain = true)
@ApiModel(value = "user对象", description = "user对象")
public class GroupAuditOperator {
    @Id
    @CreatedBy
    @ApiModelProperty(value = "ID", hidden = true)
    private String id;
    @ApiModelProperty(value = "审计所属数据", hidden = true)
    private String auditId;
    @ApiModelProperty(value = "人员账户", hidden = true)
    private String username;
    @ApiModelProperty(value = "性别 1男 0女", hidden = true)
    private String sex;
    @ApiModelProperty(value = "真实姓名", hidden = true)
    private String realName;
    @ApiModelProperty(value = "人员密码", hidden = true)
    private String password;
    @ApiModelProperty(value = "手机号码", hidden = true)
    private String phone;
    @ApiModelProperty(value = "邮箱", hidden = true)
    private String email;
    @ApiModelProperty(value = "是否停用(1.启用;0停用;2锁定)", hidden = true)
    private String flag;
    private String openid;
    private String user_password;
    private String nick;
    @ApiModelProperty(value = "角色", hidden = true)
    private String userType;

    @ApiModelProperty(value = "密码策略ID", hidden = true)
    private String pwdPolicy;
    @ApiModelProperty(value = "审计时间", hidden = true)
    private String optTime;
    @ApiModelProperty(value = "修改0 还是 删除1", hidden = true)
    private String optMethod;
    @ApiModelProperty(value = "审计人", hidden = true)
    private String optUsername;

    @ApiModelProperty(value = "生效日期", hidden = true)
    private String effectiveDate;
    @ApiModelProperty(value = "失效日期", hidden = true)
    private String invalidDate;
    @ApiModelProperty(value = "备用1", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "备用2", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "备用3", hidden = true)
    private String beiyong3;
    @ApiModelProperty(value = "备用4", hidden = true)
    private String beiyong4;
    @ApiModelProperty(value = "备用5-临时存储角色JSON", hidden = true)
    private String beiyong5;
}
