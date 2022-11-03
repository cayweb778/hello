package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：基础档案表", description = "基础档案表")
@Table("sys_user_account")
@Data
public class SysUserAccount {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "用户ID", hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private String userId;
    @ApiModelProperty(value = "账套ID", hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private String accountId;

}
