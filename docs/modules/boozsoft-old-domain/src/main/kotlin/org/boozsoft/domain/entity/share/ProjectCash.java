package org.boozsoft.domain.entity.share;

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
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;


@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "数据表：现金流量基础项目", description = "现金流量基础项目")
@Table("project_cash")
public class ProjectCash {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "项目编码", hidden = true)
    private String projectCode;
    @ApiModelProperty(value = "项目名称", hidden = true)
    private String projectName;
    @ApiModelProperty(value = "所属类别编码（经营活动、投资活动、筹资活动、汇率活动、现金及现金等价物）", hidden = true)
    private String projectType;
    @ApiModelProperty(value = "所属类别名称", hidden = true)
    private String projectTypeName;
    @ApiModelProperty(value = "方向(流入、流出)", hidden = true)
    private String fangxiang;
    @ApiModelProperty(value = "状态(1.启用;0停用)", hidden = true)
    private String flag;
    @ApiModelProperty(value = "金额", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong4;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong5;
    @Transient
    @ApiModelProperty(value = "租户Id", position = 45)
    private String tenantId;
    @Transient
    private List<ProjectCashCode> table;

}
