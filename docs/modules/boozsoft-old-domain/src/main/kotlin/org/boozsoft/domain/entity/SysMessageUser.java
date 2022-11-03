package org.boozsoft.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ApiModel(value = "消息用户关联表", description = "消息用户关联表")
@Table("_app_group_sys_message_user")
@Data
public class SysMessageUser {

    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键", position = 0)
    private String id;

    @ApiModelProperty(value = "用户唯一码", position = 1)
    private String userId;

    @ApiModelProperty(value = "消息通知唯一码", position = 1)
    private String messageId;

    @ApiModelProperty(value = "状态 1已阅0未阅2隐藏(已阅可以隐藏)", position = 4)
    private String ifrag;

    @ApiModelProperty(value = "已阅时间", position = 5)
    private LocalDateTime redTime;
}
