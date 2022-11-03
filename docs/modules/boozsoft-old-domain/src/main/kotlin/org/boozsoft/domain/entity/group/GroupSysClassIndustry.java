package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "集团：行业性质表", description = "组织信息实体类")
@Table("_app_group_sys_class_industry")
@Data
public class GroupSysClassIndustry {
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "唯一编码 ", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "门类", hidden = true)
    private String category;
    @ApiModelProperty(value = "大类", hidden = true)
    private String bigKind;
    @ApiModelProperty(value = "中类", hidden = true)
    private String middleKind;
    @ApiModelProperty(value = "小类", hidden = true)
    private String smallKind;
    @ApiModelProperty(value = "名称", hidden = true)
    private String name;
    @ApiModelProperty(value = "说明", hidden = true)
    private String explain;
    @ApiModelProperty(value = "上级", hidden = true)
    private String pid;

    private String tenantId;

}
