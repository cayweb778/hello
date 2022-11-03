package org.boozsoft.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@ApiModel(value = "任务协作用户关联表", description = "任务协作用户关联表")
@Table("_app_group_sys_task_user")
@Data
public class SysTaskUser {

    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键", position = 0)
    private String id;

    @ApiModelProperty(value = "用户唯一码", position = 1)
    private String userId;

    @ApiModelProperty(value = "任务协作唯一码", position = 1)
    private String messageId;

    @ApiModelProperty(value = "状态 1已完成0未完成2作废(已完成可以作废)", position = 4)
    private String ifrag;

    @ApiModelProperty(value = "已阅时间", position = 5)
    private LocalDateTime redTime;
}
