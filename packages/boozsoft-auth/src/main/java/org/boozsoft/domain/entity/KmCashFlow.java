package org.boozsoft.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "现金流量科目", description = "现金流量科目")
@Table("km_cash_flow")
@Data
public class KmCashFlow {

    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键", position = 0)
    private String id;

    @ApiModelProperty(value = "会计年度")
    private String iyear;

    @ApiModelProperty(value = "科目编码")
    private String ccode;

    @ApiModelProperty(value = "租户Id", position = 45)
    private String tenantId;

}
