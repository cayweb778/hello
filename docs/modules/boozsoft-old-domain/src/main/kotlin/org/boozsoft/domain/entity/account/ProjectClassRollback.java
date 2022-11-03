package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：项目分类回滚表", description = "项目分类回滚表")
@Table("project_class_rollback")
@Data
public class ProjectClassRollback {

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
    private Long parentId;
    @ApiModelProperty(value = "是否末级（0.不是；1是）", hidden = true)
    private String bend;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;
    @ApiModelProperty(value = "变动日期", hidden = true)
    private String biandongDate;
    @ApiModelProperty(value = "变动方式(1.修改;2.删除;3.停用)", hidden = true)
    private String biandongMethod;
    @ApiModelProperty(value = "操作员姓名", hidden = true)
    private String biandongName;
    @ApiModelProperty(value = "操作员唯一标识", hidden = true)
    private String biandongUniqueCode;
    @ApiModelProperty(value = "变动id", hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private String biandongId;

}
