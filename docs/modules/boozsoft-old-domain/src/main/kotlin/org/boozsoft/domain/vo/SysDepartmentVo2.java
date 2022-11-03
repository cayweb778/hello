package org.boozsoft.domain.vo;

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

@ApiModel(value = "数据表：部门表", description = "部门表")
@Data
@NoArgsConstructor
public class SysDepartmentVo2 {
    private String id;
    private String ccode;
    @ApiModelProperty(value = "唯一标识", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "部门编码（不允许重复）", hidden = true)
    private String deptCode;
    @ApiModelProperty(value = "部门名称", hidden = true)
    private String deptName;
    @ApiModelProperty(value = "上级部门ID", hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private String parentId;
    @ApiModelProperty(value = "负责人：员工唯一编码", hidden = true)
    private String uniqueCodeUser;
    @ApiModelProperty(value = "创建日期", hidden = true)
    private String createDate;
    @ApiModelProperty(value = "是否停用(1.启用;0停用)", hidden = true)
    private String flag;
    @ApiModelProperty(value = "租户ID", position = 21)
    private String tenantId;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;
}
