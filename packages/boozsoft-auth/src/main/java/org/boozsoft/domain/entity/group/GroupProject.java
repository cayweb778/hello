package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：项目目录表", description = "项目目录表")
@Data
@Table("_app_group_project")
public class GroupProject {
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "唯一标识", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "项目编码", hidden = true)
    private String projectCode;
    @ApiModelProperty(value = "项目名称", hidden = true)
    private String projectName;
    @ApiModelProperty(value = "所属项目分类编码", hidden = true)
    private String projectClassCode;
    @ApiModelProperty(value = "是否结算", hidden = true)
    private String jiesuan;
    @ApiModelProperty(value = "开始日期", hidden = true)
    private String startDate;
    @ApiModelProperty(value = "结束日期", hidden = true)
    private String endDate;
    @ApiModelProperty(value = "项目负责人（人员唯一编码）", hidden = true)
    private String psnInCharge;
    @ApiModelProperty(value = "所属部门唯一编码", hidden = true)
    private String deptCode;

    @ApiModelProperty(value = "项目样式编码", hidden = true)
    private String projectCateCode;
    @ApiModelProperty(value = "项目大类编码", hidden = true)
    private String itemCode;

    @ApiModelProperty(value = "生效状态（1是；0否；-1驳回）", hidden = true)
    private String successState;
    @ApiModelProperty(value = "申请人", hidden = true)
    private String applyUser;
    @ApiModelProperty(value = "申请时间", hidden = true)
    private String applyDate;
    @ApiModelProperty(value = "审批人", hidden = true)
    private String approveUser;
    @ApiModelProperty(value = "审批时间", hidden = true)
    private String approveDate;
    @ApiModelProperty(value = "组织ID", hidden = true)
    private String originId;
    @ApiModelProperty(value = "租户ID", hidden = true)
    private String tenantId;
    @ApiModelProperty(value = "驳回原因", hidden = true)
    private String reason;
    @ApiModelProperty(value = "申请类型(1,组织;2,账套)", hidden = true)
    private String ctype;

    @ApiModelProperty(value = "是否删除", hidden = true)
    private String isDel;
    @ApiModelProperty(value = "删除人姓名", hidden = true)
    private String delName;
    @ApiModelProperty(value = "删除时间", hidden = true)
    private String delDate;

    @ApiModelProperty(value = "自定义字段1", hidden = true)
    private String menu1;
    @ApiModelProperty(value = "自定义字段2", hidden = true)
    private String menu2;
    @ApiModelProperty(value = "自定义字段3", hidden = true)
    private String menu3;
    @ApiModelProperty(value = "自定义字段4", hidden = true)
    private String menu4;
    @ApiModelProperty(value = "自定义字段5", hidden = true)
    private String menu5;
    @ApiModelProperty(value = "自定义字段6", hidden = true)
    private String menu6;
    @ApiModelProperty(value = "自定义字段7", hidden = true)
    private String menu7;
    @ApiModelProperty(value = "自定义字段8", hidden = true)
    private String menu8;
    @ApiModelProperty(value = "自定义字段9", hidden = true)
    private String menu9;
    @ApiModelProperty(value = "自定义字段10", hidden = true)
    private String menu10;
    @ApiModelProperty(value = "自定义字段11", hidden = true)
    private String menu11;
    @ApiModelProperty(value = "自定义字段12", hidden = true)
    private String menu12;
    @ApiModelProperty(value = "自定义字段13", hidden = true)
    private String menu13;
    @ApiModelProperty(value = "自定义字段14", hidden = true)
    private String menu14;
    @ApiModelProperty(value = "自定义字段15", hidden = true)
    private String menu15;
    @ApiModelProperty(value = "自定义字段16", hidden = true)
    private String menu16;
    @ApiModelProperty(value = "自定义字段17", hidden = true)
    private String menu17;
    @ApiModelProperty(value = "自定义字段18", hidden = true)
    private String menu18;
    @ApiModelProperty(value = "自定义字段19", hidden = true)
    private String menu19;
    @ApiModelProperty(value = "自定义字段20", hidden = true)
    private String menu20;
    @ApiModelProperty(value = "自定义字段21", hidden = true)
    private String menu21;
    @ApiModelProperty(value = "自定义字段22", hidden = true)
    private String menu22;
    @ApiModelProperty(value = "自定义字段23", hidden = true)
    private String menu23;
    @ApiModelProperty(value = "自定义字段24", hidden = true)
    private String menu24;
    @ApiModelProperty(value = "自定义字段25", hidden = true)
    private String menu25;
    @ApiModelProperty(value = "自定义字段26", hidden = true)
    private String menu26;
    @ApiModelProperty(value = "自定义字段27", hidden = true)
    private String menu27;
    @ApiModelProperty(value = "自定义字段28", hidden = true)
    private String menu28;
    @ApiModelProperty(value = "自定义字段29", hidden = true)
    private String menu29;
    @ApiModelProperty(value = "自定义字段30", hidden = true)
    private String menu30;
    @ApiModelProperty(value = "自定义字段31", hidden = true)
    private String menu31;
    @ApiModelProperty(value = "自定义字段32", hidden = true)
    private String menu32;
    @ApiModelProperty(value = "自定义字段33", hidden = true)
    private String menu33;
    @ApiModelProperty(value = "自定义字段34", hidden = true)
    private String menu34;
    @ApiModelProperty(value = "自定义字段35", hidden = true)
    private String menu35;
    @ApiModelProperty(value = "自定义字段36", hidden = true)
    private String menu36;
    @ApiModelProperty(value = "自定义字段37", hidden = true)
    private String menu37;
    @ApiModelProperty(value = "自定义字段38", hidden = true)
    private String menu38;
    @ApiModelProperty(value = "自定义字段39", hidden = true)
    private String menu39;
    @ApiModelProperty(value = "自定义字段40", hidden = true)
    private String menu40;
    @ApiModelProperty(value = "自定义字段41", hidden = true)
    private String menu41;
    @ApiModelProperty(value = "自定义字段42", hidden = true)
    private String menu42;
    @ApiModelProperty(value = "自定义字段43", hidden = true)
    private String menu43;
    @ApiModelProperty(value = "自定义字段44", hidden = true)
    private String menu44;
    @ApiModelProperty(value = "自定义字段45", hidden = true)
    private String menu45;
    @ApiModelProperty(value = "自定义字段46", hidden = true)
    private String menu46;
    @ApiModelProperty(value = "自定义字段47", hidden = true)
    private String menu47;
    @ApiModelProperty(value = "自定义字段48", hidden = true)
    private String menu48;
    @ApiModelProperty(value = "自定义字段49", hidden = true)
    private String menu49;
    @ApiModelProperty(value = "自定义字段50", hidden = true)
    private String menu50;
}
