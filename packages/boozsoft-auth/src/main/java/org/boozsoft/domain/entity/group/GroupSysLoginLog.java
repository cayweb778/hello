package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：操作日志主表", description = "操作日志主表")
@Table("_app_group_sys_login_log")
@Data
public class GroupSysLoginLog {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "操作时间（年月日时分秒）", hidden = true)
    private String loginTime;
    @ApiModelProperty(value = "操作员ID", hidden = true)
    private String userId;
    @ApiModelProperty(value = "操作员名称", hidden = true)
    private String userName;
    @ApiModelProperty(value = "操作模块（集团group、组织org、主数据master_data、总账gl、现金银行cashier、固定资产fa、存货管理stock、报表中心bb）", hidden = true)
    private String optModule;
    @ApiModelProperty(value = "操作功能（具体菜单对应功能，如会计科目、记账凭证、客户、供应商、人员、部门、项目、自定义项）", hidden = true)
    private String optFunction;
    @ApiModelProperty(value = "范围（1账套，0组织，2集团）", hidden = true)
    private String optRange;
    @ApiModelProperty(value = "范围唯一码（账套唯一码或组织唯一码）", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "单据日期(年月日)", hidden = true)
    private String billDate;
    @ApiModelProperty(value = "动作（登录、退出、查询、审核、签字、编辑、新增、新增申请、修改、保存、删除、合并、导入、引入、导出、打印、分配、栏目设置）", hidden = true)
    private String optAction;
    @ApiModelProperty(value = "操作内容【新增会计科目】,科目编码【1002】,科目名称【银行存款】,科目方向【借】,科目类型【资产】", hidden = true)
    private String optContent;
    @ApiModelProperty(value = "客户浏览器信息（哪种浏览器内核+版本）", hidden = true)
    private String browserinfo;
    @ApiModelProperty(value = "客户操作系统名称或版本", hidden = true)
    private String osInfo;
    @ApiModelProperty(value = "客户端主机名称", hidden = true)
    private String clientName;
    @ApiModelProperty(value = "客户端IP", hidden = true)
    private String clientIp;
    private String accId;

}
