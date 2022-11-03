package org.boozsoft.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "任务协作附件关联表", description = "任务协作附件关联表")
@Table("_app_group_sys_task_file")
@Data
public class SysTaskFile {

    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键", position = 0)
    private String id;

    @ApiModelProperty(value = "文件上传记录表id", position = 1)
    private String fileId;

    @ApiModelProperty(value = "消息通知唯一码", position = 1)
    private String messageId;

    
}
