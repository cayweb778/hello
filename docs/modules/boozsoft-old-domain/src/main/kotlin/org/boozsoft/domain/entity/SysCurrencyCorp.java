package org.boozsoft.domain.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * 币种表
 * @Date 2021-06-17
 * @Author lz
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table("sys_currency_corp")
@ApiModel(value = "币种表", description = "币种表")
public class SysCurrencyCorp {

    @ApiModelProperty(value = "主键id", position = 0)
    @Id
    private String id;
    @ApiModelProperty(value = "账套编码）", position = 255)
    private String accountId;
    @ApiModelProperty(value = "币种编码（三位", position = 50)
    private String currId;
    @ApiModelProperty(value = "币种中文名称", position = 255)
    private String currChName;
    @ApiModelProperty(value = "币种国家", position = 255)
    private String currCountry;
    @ApiModelProperty(value = "是否本位币(1是 0否)", position = 5)
    private String standCurr;
    @ApiModelProperty(value = "是否停用(1.启用;0停用)", position = 5)
    private String currFlag;
    @ApiModelProperty(value = "租户Id", position = 45)
    private String tenantId;
    @ApiModelProperty(value = "币种单位", hidden = true)
    private String currencyUnit;
}
