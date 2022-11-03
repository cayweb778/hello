package org.boozsoft.domain.entity.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "栏目设置", description = "栏目设置")
@Table("stock_column_view_settings")
@Data
public class StockColumnViewSettings {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "账套标识", hidden = true)
    private String accountId;
    @ApiModelProperty(value = "所属菜单名称", hidden = true)
    private String owningMenuName;
    @ApiModelProperty(value = "栏目类型名称", hidden = true)
    private String columnTypeName;
    @ApiModelProperty(value = "列系统名称", hidden = true)
    private String columnName;
    @ApiModelProperty(value = "是否显示", hidden = true)
    private String ifShow;
    @ApiModelProperty(value = "列显示名称",hidden = true)
    private String displayName;
    @ApiModelProperty(value = "宽度", hidden = true)
    private String columnWidth;
    @ApiModelProperty(value = "对齐方式", hidden = true)
    private String columnAlign;
    @ApiModelProperty(value = "排序key", hidden = true)
    private String tkey;
    @ApiModelProperty(value = "所属列key标识", hidden = true)
    private String pkey;
    @ApiModelProperty(value = "用户ID", hidden = true)
    private String userId;
    @ApiModelProperty(value = "类型(0明细、1汇总)", hidden = true)
    private String isType;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;

}
