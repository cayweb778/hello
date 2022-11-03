package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：会计期间表", description = "会计期间表")
@Table("iperiod")
@Data
public class Iperiod {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "会计期间编码（只有1-12）", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "年度", hidden = true)
    private String iyear;
    @ApiModelProperty(value = "起始日期（接上月最后一天）", hidden = true)
    private String dateStart;
    @ApiModelProperty(value = "结束日期（起始日期非本月一日，则与上月相同，本月一日起始，则为本月最后一天）", hidden = true)
    private String dateEnd;
    @ApiModelProperty(value = "结账标识(0.未结账；1.已结账)", hidden = true)
    private String flag;
    @ApiModelProperty(value = "结账日期", hidden = true)
    private String checkOutDate;
    @ApiModelProperty(value = "结账人", hidden = true)
    private String checkOutPerson;
    @ApiModelProperty(value = "启用期间", hidden = true)
    private String enableIperiod;
    @ApiModelProperty(value = "区分会计区间与季度 1 为季度 其他为期间", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;

}
