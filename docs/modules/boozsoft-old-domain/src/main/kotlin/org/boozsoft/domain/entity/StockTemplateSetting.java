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
@Table("stock_template_setting" )
@ApiModel(value="存货打印模板",description="存货打印模板")
public class StockTemplateSetting {
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;// 主键
    @ApiModelProperty(value = "模板类型 0 系统 1 A4横向 2 A4纵向 3 两栏 4 三栏", position = 1)
    private String templateType;
    @ApiModelProperty(value = "模板排序", position = 1)
    private String templateSort;
    @ApiModelProperty(value = "模板名称", position = 1)
    private String templateName;
    @ApiModelProperty(value = "所属档案", position = 1)
    private String templateBelong;
    private String beiyong1;
    private String beiyong2;
    private String beiyong3;
    private String beiyong4;
    private String beiyong5;
    @Transient
    private String tenantId;// 公司唯一码

}
