package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：结算方式表", description = "结算方式表")
@Table("sys_sett_modes")
@Data
public class SettModes {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "结算方式编码（不可修改）", hidden = true)
    private String settModesCode;
    @ApiModelProperty(value = "结算方式名称", hidden = true)
    private String settModesName;
    @ApiModelProperty(value = "创建日期", hidden = true)
    private String createDate;
    @ApiModelProperty(value = "停用日期", hidden = true)
    private String stopDate;
    @ApiModelProperty(value = "是否停用(1.启用;0.停用)", hidden = true)
    private String flag;
    @ApiModelProperty(value = "默认账户", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "说明", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;
    @ApiModelProperty(value = "", hidden = true)
    @Transient
    private String tenantId;

}
