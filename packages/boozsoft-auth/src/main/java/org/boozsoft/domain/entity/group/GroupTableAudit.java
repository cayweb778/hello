package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "档案名：$$档案名$$", description = "$$描述信息$$")
@Table("_app_group_table_audit")
@Data
public class GroupTableAudit {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    @ApiModelProperty(value = "数据", hidden = true)
    private String data;

    @ApiModelProperty(value = "操作设置", hidden = true)
    private String operatMode;


    @ApiModelProperty(value = "创建时间", hidden = true)
    private String createTime;


    @ApiModelProperty(value = "作者", hidden = true)
    private String author;


    @ApiModelProperty(value = "唯一码", hidden = true)
    private String uniqueCode;


    @ApiModelProperty(value = "表名", hidden = true)
    private String tableName;





}
