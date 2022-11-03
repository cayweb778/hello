package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "档案名：$$档案名$$", description = "$$描述信息$$")
@Table("_app_group_used_foreign_currency")
@Data
@NoArgsConstructor
public class GroupUsedForeignCurrency {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
            
    @ApiModelProperty(value = "外币代码", hidden = true)
    private String foreignCode;
                

    @ApiModelProperty(value = "外币名称", hidden = true)
    private String foreignName;
                

    @ApiModelProperty(value = "外币换算规则", hidden = true)
    private String foreignSimpName;
                

    @ApiModelProperty(value = "未设置", hidden = true)
    private String accountId;
                

    @ApiModelProperty(value = "所属国家（地区）", hidden = true)
    private String beiyong1;
                

    @ApiModelProperty(value = "未设置", hidden = true)
    private String beiyong2;
                

    @ApiModelProperty(value = "未设置", hidden = true)
    private String beiyong3;
}
