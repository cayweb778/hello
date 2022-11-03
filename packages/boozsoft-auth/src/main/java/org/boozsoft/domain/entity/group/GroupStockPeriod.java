package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：存货管理期间表", description = "存货管理期间表")
@Table("_app_group_stock_period")
@Data
@EqualsAndHashCode(callSuper = false)
public class GroupStockPeriod {

    @ApiModelProperty(value = "主键id", position = 0)
    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "存货账套ID", position = 1)
    private String uniqueCode;
    @ApiModelProperty(value = "月", position = 5)
    private String stockMonth;
    @ApiModelProperty(value = "年", position = 6)
    private String stockYear;
    @ApiModelProperty(value = "年月启用", position = 6)
    private String stockFlag;
    @ApiModelProperty(value = "是否结账（1是，0否） ", position = 2)
    private String istock;
    @ApiModelProperty(value = "结账时间 ", position = 3)
    private String istockTime;
    @ApiModelProperty(value = "结账人 ", position = 4)
    private String istockUser;
    @ApiModelProperty(value = "应收是否结账（1是，0否） ", position = 2)
    private String iar;
    @ApiModelProperty(value = "应收结账时间 ", position = 3)
    private String iarTime;
    @ApiModelProperty(value = "应收结账人 ", position = 4)
    private String iarUser;
    @ApiModelProperty(value = "应付是否结账（1是，0否） ", position = 2)
    private String iap;
    @ApiModelProperty(value = "应付结账时间 ", position = 3)
    private String iapTime;
    @ApiModelProperty(value = "应付结账人 ", position = 4)
    private String iapUser;

    @ApiModelProperty(value = "是否成本核算（1是，0否） ", position = 2)
    private String cost;
    @ApiModelProperty(value = "是否制单（1是，0否） ", position = 2)
    private String billMake;

    private String beiyong1;
    private String beiyong2;
    private String beiyong3;
    private String beiyong4;
    private String beiyong5;

}


