package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：自定义项档案", description = "自定义项档案")
@Table("_app_group_defined_record")
@Data
public class GroupDefinedRecord {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "自定义项设置编码", hidden = true)
    private String definedCode;
    @ApiModelProperty(value = "档案名称", hidden = true)
    private String recordCode;
    @ApiModelProperty(value = "档案编码", hidden = true)
    private String recordName;
    @ApiModelProperty(value = "是否停用(1.启用;0停用)", hidden = true)
    private String flag;
    @ApiModelProperty(value = "说明", hidden = true)
    private String remarks;
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

    @ApiModelProperty(value = "生效状态（1是；0否；-1驳回）", hidden = true)
    private String successState;
    @ApiModelProperty(value = "申请人", hidden = true)
    private String applyUser;
    @ApiModelProperty(value = "申请时间", hidden = true)
    private String applyDate;
    @ApiModelProperty(value = "审批人", hidden = true)
    private String approveUser;
    @ApiModelProperty(value = "审批时间", hidden = true)
    private String approveDate;
    @ApiModelProperty(value = "组织ID", hidden = true)
    private String originId;
    @ApiModelProperty(value = "租户ID", hidden = true)
    private String tenantId;
    @ApiModelProperty(value = "驳回原因", hidden = true)
    private String reason;
    @ApiModelProperty(value = "申请类型(1,组织;2,账套)", hidden = true)
    private String ctype;

}
