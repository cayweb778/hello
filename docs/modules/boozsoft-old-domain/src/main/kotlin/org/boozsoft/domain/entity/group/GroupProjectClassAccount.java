package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：项目分类分配表", description = "项目分类分配表")
@Table("_app_group_project_class_account")
@Data
public class GroupProjectClassAccount {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "唯一编码", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "分配类型(1,组织;2,账套)", hidden = true)
    private String ctype;
    @ApiModelProperty(value = "组织ID", hidden = true)
    private String originId;
    @ApiModelProperty(value = "租户ID", hidden = true)
    private String tenantId;
    @ApiModelProperty(value = "分配人", hidden = true)
    private String assignUser;
    @ApiModelProperty(value = "分配时间", hidden = true)
    private String assignDate;
    @ApiModelProperty(value = "引入状态(0,未引入;1,已引入)", hidden = true)
    private String flag;
    @ApiModelProperty(value = "引入人", hidden = true)
    private String acceptUser;
    @ApiModelProperty(value = "引入时间", hidden = true)
    private String acceptDate;
    @ApiModelProperty(value = "项目大类编码", hidden = true)
    private String itemCode;

}
