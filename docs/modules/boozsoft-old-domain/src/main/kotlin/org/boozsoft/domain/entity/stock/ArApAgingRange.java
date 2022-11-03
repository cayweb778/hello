package org.boozsoft.domain.entity.stock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：账龄区间表", description = "账龄区间表")
@Table("ar_ap_aging_range")
@Data
public class ArApAgingRange {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @Transient
    @ApiModelProperty(value = "公司唯一码",hidden = true)
    private String tenantId;
    @ApiModelProperty(value = "账龄种类：应收(ar)、应付(ap)", hidden = true)
    private String agingModel;
    @ApiModelProperty(value = "所属账套", hidden = true)
    private String accId;
    @ApiModelProperty(value = "起止天数", hidden = true)
    private String startDayNumber;
    @ApiModelProperty(value = "总天数", hidden = true)
    private String totalDayNumber;

    @ApiModelProperty(value = "", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;
}
