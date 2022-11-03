package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "查询方案存储", description = "查询存储")
@Table("_app_group_query_plan")
@Data
public class GroupQueryPlan {
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    @ApiModelProperty(value = "账套标识", hidden = true)
    private String accountId;

    @ApiModelProperty(value = "所属菜单名称", hidden = true)
    private String owningMenuName;

    @ApiModelProperty(value = "方案类型 1 个人 0集团", hidden = true)
    private String owningPlan;

    @ApiModelProperty(value = "方案所属人string", hidden = true)
    private String planPerson;

    @ApiModelProperty(value = "可用查询条件[]", hidden = true)
    private String filterConditions;

    @ApiModelProperty(value = "查询条件json", hidden = true)
    private String queryConditions;

    private String beiyong1;

    private String beiyong2;

    private String beiyong3;
}
