package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：操作员登陆信息表", description = "操作员登陆信息表")
@Table("sys_login_opt")
@Data
public class SysLoginOpt {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "操作员id", hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private String userId;
    @ApiModelProperty(value = "登陆时间（选择日期、默认当前日期）", hidden = true)
    private String loginDate;
    @ApiModelProperty(value = "登陆集团或公司编码", hidden = true)
    private String loginFlag;

}
