package org.boozsoft.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

@ApiModel(value = "消息通知", description = "消息通知")
@Table("_app_group_sys_message")
@Data
public class SysMessage {

    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键", position = 0)
    private String id;

    @ApiModelProperty(value = "内容", position = 1)
    private String content;

    @ApiModelProperty(value = "发送时间", position = 2)
    private LocalDateTime sendTime;

    @ApiModelProperty(value = "发送人", position = 3)
    private String sender;

    @ApiModelProperty(value = "优先级 四象限", position = 5)
    private String weights;

    @ApiModelProperty(value = "唯一码", position = 6)
    private String messageId;

    @ApiModelProperty(value = "消息类型唯一码", position = 6)
    private String typeId;

    //用户id list
    @Transient
    ArrayList<String> user =  new ArrayList<>();
    //fileid list
    @Transient
    ArrayList<String> file =  new ArrayList<>();
}
