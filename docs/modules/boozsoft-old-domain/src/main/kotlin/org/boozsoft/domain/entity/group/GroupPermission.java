package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：集团档案分配权限表", description = "集团档案分配权限表")
@Table("_app_group_permission")
@Data
public class GroupPermission {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "操作员ID", hidden = true)
    private String userId;
    @ApiModelProperty(value = "组织ID", hidden = true)
    private String originId;
    @ApiModelProperty(value = "租户ID", hidden = true)
    private String tenantId;
    @ApiModelProperty(value = "是否新增自动生效(0,否;1,是)", hidden = true)
    private String isAuto;
    @ApiModelProperty(value = "是否审批", hidden = true)
    private String isApprove;
    @ApiModelProperty(value = "是否主管", hidden = true)
    private String supervisor;
    @ApiModelProperty(value = "基础档案名称", hidden = true)
    private String baseName;
    @ApiModelProperty(value = "基础档案数据表名称", hidden = true)
    private String baseEnName;
    @ApiModelProperty(value = "申请类型(1,组织;2,账套)", hidden = true)
    private String ctype;

}
