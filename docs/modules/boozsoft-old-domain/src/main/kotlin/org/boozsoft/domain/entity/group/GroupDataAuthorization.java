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

@ApiModel(value = "数据表：数据权限表", description = "数据权限表")
@Table("_app_group_data_authorization")
@Data
@Accessors(chain = true)
public class GroupDataAuthorization {
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "档案Id", hidden = true)
    private String archivesId;
    @ApiModelProperty(value = "数据ID", hidden = true)
    private String dataId;
    @ApiModelProperty(value = "操作员ID", hidden = true)
    private String operatorId;
    @ApiModelProperty(value = "账套租户", hidden = true)
    private String tenantryId;
    @ApiModelProperty(value = "年度 暂时遗弃", hidden = true)
    private String iyear;
    @ApiModelProperty(value = "档案主管 1 是", hidden = true)
    private String isDirector;
    private String beiyong1;
    private String beiyong2;
    private String beiyong3;

}
