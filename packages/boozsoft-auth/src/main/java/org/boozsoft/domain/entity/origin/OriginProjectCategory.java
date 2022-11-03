package org.boozsoft.domain.entity.origin;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：项目大类表", description = "项目大类表")
@Table("_app_origin_project_category")
@Data
public class OriginProjectCategory {

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

    @ApiModelProperty(value = "是否停用(1.启用;0停用)", hidden = true)
    private String flag;
    @ApiModelProperty(value = "组织ID", hidden = true)
    private String originId;
}
