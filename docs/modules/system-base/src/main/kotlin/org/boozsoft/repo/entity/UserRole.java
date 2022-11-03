package org.boozsoft.repo.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 实体类
 *
 * @author Chill
 */
@Data
@Table("_app_group_sys_user_role")
@Accessors(chain = true)
@ApiModel(value = "UserRole对象", description = "用户角色对象")
public class UserRole {

    @Id
    @ApiModelProperty(value = "主键")
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "用户id")
    @JsonSerialize(using = ToStringSerializer.class)
    private String userId;
    @ApiModelProperty(value = "角色id")
    @JsonSerialize(using = ToStringSerializer.class)
    private String roleId;

}
