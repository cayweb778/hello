package org.boozsoft.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("fa_card_column")
@ApiModel(value = "资产卡片自定义栏目设置", description = "资产卡片自定义栏目设置")
public class FaCardColumn {

    @ApiModelProperty(value = "主键id", position = 0)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    @Id
    private String id;

    @ApiModelProperty(value = "唯一编码 ", position = 1)
    private String uniqueCode;

    @ApiModelProperty(value = "栏目名称 ", position = 2)
    private String columnName;

    @ApiModelProperty(value = "显示名称 ", position = 3)
    private String showName;

    @ApiModelProperty(value = "数据类型 0文本 1整数 2数值 3日期 ", position = 4)
    private String dataType;

    @ApiModelProperty(value = "字符长度 ", position = 4)
    private String charLength;

    @ApiModelProperty(value = "小数位 ", position = 5)
    private String decimalPlaces;

    @ApiModelProperty(value = "类型1资产信息栏目2机器设备栏目3土地建筑物栏目 ", position = 6)
    private String flag;

    @ApiModelProperty(value = " 状态 ", position = 7)
    private String status;


}
