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

import java.time.LocalDateTime;
import java.util.ArrayList;

@ApiModel(value = "任务协作", description = "任务协作")
@Table("_app_group_sys_task")
@Data
public class SysTask {

    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键", position = 0)
    private String id;

    @ApiModelProperty(value = "内容", position = 1)
    private String title;

    @ApiModelProperty(value = "内容", position = 1)
    private String content;

    @ApiModelProperty(value = "发送时间", position = 2)
    private LocalDateTime sendTime;

    @ApiModelProperty(value = "发送人", position = 3)
    private String sender;

    @ApiModelProperty(value = "状态 1已完成0未完成2作废(已完成可以作废)", position = 4)
    private String ifrag;

    @ApiModelProperty(value = "优先级 四象限", position = 5)
    private String weights;

    @ApiModelProperty(value = "唯一码", position = 6)
    private String messageId;

    @ApiModelProperty(value = "类型 1普通 2协同", position = 3)
    private String style = "1";

    //用户id list
    @Transient
    ArrayList<String> user =  new ArrayList<>();
    //fileid list
    @Transient
    ArrayList<FtpFile> file =  new ArrayList<>();


}
