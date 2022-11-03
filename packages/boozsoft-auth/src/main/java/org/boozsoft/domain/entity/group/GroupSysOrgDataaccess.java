package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "集团：组织信息授权", description = "组织信息授权实体类")
@Table("_app_group_sys_org_dataaccess")
@Data
@Accessors(chain = true)
public class GroupSysOrgDataaccess {
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "操作员ID ", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "组织唯一码", hidden = true)
    private String orgUniqueCode;
    @ApiModelProperty(value = "主管 1", hidden = true)
    private String supervisor;

}
