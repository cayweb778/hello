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

@ApiModel(value = "数据表：项目分类表", description = "项目分类表")
@Table("project_class")
@Data
@NoArgsConstructor
public class ProjectClass {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "唯一标识", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "项目分类编码", hidden = true)
    private String projectClassCode;
    @ApiModelProperty(value = "项目分类名称", hidden = true)
    private String projectClassName;
    @ApiModelProperty(value = "所属项目大类编码", hidden = true)
    private String projectCateCode;
    @ApiModelProperty(value = "上级项目分类ID", hidden = true)
    private String parentId;
    @ApiModelProperty(value = "是否末级（0.不是；1是）", hidden = true)
    private String bend;
    @ApiModelProperty(value = "级次", hidden = true)
    private String jici;
    @Transient
    @ApiModelProperty(value = "租户Id", position = 45)
    private String tenantId;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;

    @Transient
    @ApiModelProperty(value = "项目大类控制科目及项目（1是，0否，默认为否）", hidden = true)
    private String projectClassCtl;

    public ProjectClass(String uniqueCode, String projectClassCode, String projectClassName, String projectCateCode, String parentId, String bend) {
        this.uniqueCode = uniqueCode;
        this.projectClassCode = projectClassCode;
        this.projectClassName = projectClassName;
        this.projectCateCode = projectCateCode;
        this.parentId = parentId;
        this.bend = bend;
    }
}
