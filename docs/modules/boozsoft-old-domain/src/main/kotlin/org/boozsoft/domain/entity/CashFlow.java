package org.boozsoft.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "现金流量", description = "现金流量")
@Table("cash_flow")
@Data
public class CashFlow {

    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键", position = 0)
    private String id;

    @ApiModelProperty(value = "会计年度")
    private String iyear;

    @ApiModelProperty(value = "会计区间")
    private String iperiod;

    @ApiModelProperty(value = "科目编码")
    private String ccode;

    @ApiModelProperty(value = "租户Id", position = 45)
    private String tenantId;

    @ApiModelProperty(value = "凭证唯一编码", position = 1)
    private String uniqueCode;

    @ApiModelProperty(value = "对方科目", position = 1)
    private String dccode;

    @ApiModelProperty(value = "流量项目", position = 1)
    private String projectCode;

    @ApiModelProperty(value = "本币", position = 1)
    private String money;

}
