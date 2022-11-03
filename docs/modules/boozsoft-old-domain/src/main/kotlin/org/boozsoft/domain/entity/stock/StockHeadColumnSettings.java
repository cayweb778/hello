package org.boozsoft.domain.entity.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "表头栏目设置", description = "表头栏目设置")
@Table("stock_column_settings")
@Data
public class StockHeadColumnSettings {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "所属菜单名称", hidden = true)
    private String owningMenuName;
    @ApiModelProperty(value = "栏目类型", hidden = true)
    private String columnTypeName;
    @ApiModelProperty(value = "属性名称", hidden = true)
    private String fieldName;
    @ApiModelProperty(value = "标题名称", hidden = true)
    private String labelName;
    @ApiModelProperty(value = "输入类型", hidden = true)
    private String inputType;
    @ApiModelProperty(value = "参数来源", hidden = true)
    private String paraSource;
    @ApiModelProperty(value = "是否必填 1", hidden = true)
    private String required;
    @ApiModelProperty(value = "是否显示 1", hidden = true)
    private String isShow;
    @ApiModelProperty(value = "序号", hidden = true)
    private Long serial;

    private String beiyong1;

    private String beiyong2;

    private String beiyong3;
}
