package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：项目大类表", description = "项目大类表")
@Table("project_category")
@Data
@NoArgsConstructor
public class ProjectCategory {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "项目大类编码", hidden = true)
    private String projectCateCode;
    @ApiModelProperty(value = "项目大类名称", hidden = true)
    private String projectCateName;
    @ApiModelProperty(value = "项目目录表名称", hidden = true)
    private String projectTable;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;

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
    @ApiModelProperty(value = "变动方式(1.增加;2.修改)", hidden = true)
    private String biandongMethod;
    @ApiModelProperty(value = "是否停用(1.启用;0停用)", hidden = true)
    private String flag;
    @Transient
    @ApiModelProperty(value = "租户Id", position = 45)
    private String tenantId;

    public ProjectCategory(String projectCateCode, String projectCateName, String projectTable, String successState, String flag) {
        this.projectCateCode = projectCateCode;
        this.projectCateName = projectCateName;
        this.projectTable = projectTable;
        this.successState = successState;
        this.flag = flag;
    }

}
