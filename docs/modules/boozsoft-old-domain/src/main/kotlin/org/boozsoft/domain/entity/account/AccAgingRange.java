package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：账龄区间表", description = "账龄区间表")
@Table("acc_aging_range")
@Data
public class AccAgingRange {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    @ApiModelProperty(value = "账龄种类：客户(CUSTOMER)、供应商(SUPPLIER)与个人往来（PERSONAL）", hidden = true)
    private String agingModel;
    @ApiModelProperty(value = "所属账套", hidden = true)
    private String accId;
    @ApiModelProperty(value = "起止天数", hidden = true)
    private String startDayNumber;
    @ApiModelProperty(value = "总天数", hidden = true)
    private String totalDayNumber;

    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;
}
