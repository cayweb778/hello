//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.boozsoft.domain.entity.group;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Table("_app_group_sys_user")
@ApiModel(value = "user对象", description = "user对象")
public class GroupUser implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @CreatedBy
    @ApiModelProperty(value = "ID", hidden = true)
    private String id;
    @ApiModelProperty(value = "人员账户", hidden = true)
    private String username;
    @ApiModelProperty(value = "真实姓名", hidden = true)
    private String realName;
    @ApiModelProperty(value = "性别", hidden = true)
    private String sex;
    @ApiModelProperty(value = "人员密码", hidden = true)
    private String password;
    @ApiModelProperty(value = "密码时间", hidden = true)
    private String passwordTime;
    @ApiModelProperty(value = "手机号码", hidden = true)
    private String phone;
    @ApiModelProperty(value = "邮箱", hidden = true)
    private String email;
    @ApiModelProperty(value = "是否停用(1.启用;0停用;2锁定)", hidden = true)
    private String flag;
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
    @ApiModelProperty(value = "备用5", hidden = true)
    private String openid;

    @ApiModelProperty(value = "密码策略ID", hidden = true)
    private String pwdPolicy;

    @ApiModelProperty(value = "生效日期", hidden = true)
    private String effectiveDate;

    @ApiModelProperty(value = "失效日期", hidden = true)
    private String invalidDate;

    @ApiModelProperty(value = "锁定时间", hidden = true)
    private String lockTime;

    @ApiModelProperty(value = "最后登录时间", hidden = true)
    private String lastLoginTime;

    @ApiModelProperty(value = "新建时间", hidden = true)
    private String createTime;

    @ApiModelProperty(value = "旧密码", hidden = true)
    private String passwordOld;
    @ApiModelProperty(value = "旧密码时间", hidden = true)
    private String passwordOldTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getBeiyong1() {
        return beiyong1;
    }

    public void setBeiyong1(String beiyong1) {
        this.beiyong1 = beiyong1;
    }

    public String getBeiyong2() {
        return beiyong2;
    }

    public void setBeiyong2(String beiyong2) {
        this.beiyong2 = beiyong2;
    }

    public String getBeiyong3() {
        return beiyong3;
    }

    public void setBeiyong3(String beiyong3) {
        this.beiyong3 = beiyong3;
    }

    public String getBeiyong4() {
        return beiyong4;
    }

    public void setBeiyong4(String beiyong4) {
        this.beiyong4 = beiyong4;
    }

    public String getBeiyong5() {
        return beiyong5;
    }

    public void setBeiyong5(String beiyong5) {
        this.beiyong5 = beiyong5;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getPwdPolicy() {
        return pwdPolicy;
    }

    public void setPwdPolicy(String pwdPolicy) {
        this.pwdPolicy = pwdPolicy;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getInvalidDate() {
        return invalidDate;
    }

    public void setInvalidDate(String invalidDate) {
        this.invalidDate = invalidDate;
    }

    @Transient
    private String label;

    @Transient
    private String value;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public String getLockTime() {
        return lockTime;
    }

    public void setLockTime(String lockTime) {
        this.lockTime = lockTime;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPasswordTime() {
        return passwordTime;
    }

    public void setPasswordTime(String passwordTime) {
        this.passwordTime = passwordTime;
    }

    public String getPasswordOld() {
        return passwordOld;
    }

    public void setPasswordOld(String passwordOld) {
        this.passwordOld = passwordOld;
    }

    public String getPasswordOldTime() {
        return passwordOldTime;
    }

    public void setPasswordOldTime(String passwordOldTime) {
        this.passwordOldTime = passwordOldTime;
    }
}
