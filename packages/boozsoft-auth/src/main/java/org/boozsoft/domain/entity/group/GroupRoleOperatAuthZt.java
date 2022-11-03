package org.boozsoft.domain.entity.group;


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
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table("_group_role_operat_auth_zt")
@ApiModel(value = "角色账套权限表", description = "角色账套权限表")
public class GroupRoleOperatAuthZt {
    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键", position = 0)
    private String id;
    @ApiModelProperty(value = "账套ID", position = 1)
    private String ztUniqueCode;
    @ApiModelProperty(value = "账套类别（ 财务会计ZZ、固定资产GD、存货管理CH）", position = 2)
    private String ztStyle;
    @ApiModelProperty(value = "授权年度", position = 3)
    private String ztYear;
    @ApiModelProperty(value = "角色ID", position = 4)
    private String roleUniqueCode;
    @ApiModelProperty(value = "账套主管 1 ", position = 5)
    private String supervisor;
    @ApiModelProperty(value = "默认登录账套 1 ", position = 6)
    private String defaultLogin;
}
