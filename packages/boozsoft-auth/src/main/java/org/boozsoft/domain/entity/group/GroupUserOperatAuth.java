package org.boozsoft.domain.entity.group;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table("_group_user_operat_auth")
@ApiModel(value = "操作员账套权限表", description = "操作员账套权限表")
public class GroupUserOperatAuth {
    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键", position = 0)
    private String id;
    @ApiModelProperty(value = "平台ID")
    private String  platformMark;
    @ApiModelProperty(value = "权限表user_operat_auth_zt)主键")
    private String  usrAuthId;
    @ApiModelProperty(value = "功能代码名称（如填制凭证、科目期初余额）")
    private String  functionName;
    @ApiModelProperty(value = "功能菜单对应ID")
    private String  functionId;
    @ApiModelProperty(value = "查看（1是，0否）")
    private String  isSee;
    @ApiModelProperty(value = "编辑（1是，0否）")
    private String  isEdit;
    @ApiModelProperty(value = "新增（1是，0否）")
    private String  isAdd;
    @ApiModelProperty(value = "修改（1是，0否）")
    private String  isChange;
    @ApiModelProperty(value = "删除（1是，0否）")
    private String  isDel;
    @ApiModelProperty(value = "操作（1是，0否）")
    private String  isOprat;
    @ApiModelProperty(value = "插入（1是，0否）")
    private String  isInsert;


    @ApiModelProperty(value = "审核/核销（1是，0否）")
    private String  isSign;
    @ApiModelProperty(value = "弃审/取消（1是，0否）")
    private String  isCancel;

    @ApiModelProperty(value = "出纳签字（1是，0否）")
    private String  isCashierSign;
    @ApiModelProperty(value = "出纳签字（1是，0否）")
    private String  isCashierCancel;

    @ApiModelProperty(value = "主管签字（1是，0否）")
    private String  isSupervisorSign;
    @ApiModelProperty(value = "出纳签字（1是，0否）")
    private String  isSupervisorCancel;

    @ApiModelProperty(value = "记账（1是，0否）")
    private String  isBook;

    @ApiModelProperty(value = "作废（1是，0否）")
    private String  isTovoid;
    @ApiModelProperty(value = "生单（1是，0否）")
    private String  isMake;
    @ApiModelProperty(value = "导入（1是，0否）")
    private String  isImport;
    @ApiModelProperty(value = "导出（1是，0否）")
    private String  isExport;
    @ApiModelProperty(value = "打印（1是，0否）")
    private String  isPrint;
    @ApiModelProperty(value = "发送邮件（1是，0否）")
    private String  isEmail;
    @ApiModelProperty(value = "栏目设置（1是，0否）")
    private String  isColumn;
    @ApiModelProperty(value = "启用/生效（1是，0否）")
    private String  isStart;
    @ApiModelProperty(value = "停用/失效（1是，0否）")
    private String  isStop;


    @ApiModelProperty(value = "设置（1是，0否）")
    private String  isSetting;
    @ApiModelProperty(value = "附件查看（1是，0否）")
    private String  isAnnexSee;
    @ApiModelProperty(value = "附件修改（1是，0否）")
    private String  isAnnexEdit;
    @ApiModelProperty(value = "附件变更（1是，0否）")
    private String  isAnnexChange;
    @ApiModelProperty(value = "回收（1是，0否）")
    private String  isRecycle;
    @ApiModelProperty(value = "保存（1是，0否）")
    private String  isSave;

    
    
    
    private String  beiyong1;
    private String  beiyong2;
    private String  beiyong3;
    private String  beiyong4;
    private String  beiyong5;
    private String  beiyong6;
    private String  beiyong7;
    private String  beiyong8;
    private String  beiyong9;
    private String  beiyong10;
}
