package org.boozsoft.domain.entity.origin;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：项目栏目表", description = "项目栏目表")
@Table("_app_origin_project_column")
@Data
public class OriginProjectColumn {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "项目大类编码", hidden = true)
    private String projectCateCode;
    @ApiModelProperty(value = "字段排序号", hidden = true)
    private Integer num;
    @ApiModelProperty(value = "字段名称", hidden = true)
    private String cname;
    @ApiModelProperty(value = "字段标题", hidden = true)
    private String ctitle;
    @ApiModelProperty(value = "字段属性", hidden = true)
    private String shuxing;
    @ApiModelProperty(value = "数据类型", hidden = true)
    private String ctype;
    @ApiModelProperty(value = "字段长度", hidden = true)
    private String clength;
    @ApiModelProperty(value = "宽度", hidden = true)
    private String cwidth;
    @ApiModelProperty(value = "数据来源类型", hidden = true)
    private String sourceType;
    @ApiModelProperty(value = "来源表名称", hidden = true)
    private String sourceName;
    @ApiModelProperty(value = "来源表关联字段", hidden = true)
    private String sourceColumnUnique;
    @ApiModelProperty(value = "来源表显示字段", hidden = true)
    private String sourceColumn;
    @ApiModelProperty(value = "是否显示（1.显示；0.不显示）", hidden = true)
    private String islist;
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
    @ApiModelProperty(value = "组织ID", hidden = true)
    private String originId;

}
