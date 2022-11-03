package org.boozsoft.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;


@Data
@Accessors(chain = true)
@Table("stock_template_settings" )
@ApiModel(value="存货打印模板明细",description="存货打印模板明细")
public class StockTemplateSettings {
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;// 主键
    @ApiModelProperty(value = "系统名称")
    private String   systemName;
    @ApiModelProperty(value = "打印名称")
    private String   printName;
    @ApiModelProperty(value = "对齐方式")
    private String   alignWay;
    @ApiModelProperty(value = "表体列宽度")
    private String   printWidth;
    @ApiModelProperty(value = "字体大小")
    private String  fontSize;
    @ApiModelProperty(value = "所属区域 0 大标题 1表头 2列标题 3表尾")
    private String  belongArea;
    @ApiModelProperty(value = "排序")
    private String  paintSort;
    @ApiModelProperty(value = "属性名称")
    private String propertyName;
    @ApiModelProperty(value = "是否打印")
    private String isPrint;
    @ApiModelProperty(value = "是否打印")
    private String parentId;
    
    private String beiyong1;
    private String beiyong2;
    private String beiyong3;
    private String beiyong4;
    private String beiyong5;
    @Transient
    private String tenantId;// 公司唯一码

}
