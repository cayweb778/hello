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

@ApiModel(value = "往来核销", description = "往来核销")
@Table("write_off")
@Data
public class WriteOff {

    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键", position = 0)
    private String id;

    @ApiModelProperty(value = "唯一编码", position = 1)
    private String uniqueCode;

    @ApiModelProperty(value = "分录唯一码", position = 1)
    private String vouchUnCode;

    @ApiModelProperty(value = "核销人", position = 1)
    private String createCode; 

    @ApiModelProperty(value = "核销日期", position = 1)
    private String hxDate;

    @ApiModelProperty(value = "租户Id", position = 45)
    private String tenantId;

    @ApiModelProperty(value = "核销码", position = 1)
    private String hxCode;

    @ApiModelProperty(value = "核销金额", position = 1)
    private String hxMoney;

    @Transient
    private String isEnd;
    @Transient
    private String dbillDate;
    @Transient
    private String inoId;
    @Transient
    private String cdigest;
    @Transient
    private String md;
    @Transient
    private String mc;
    @Transient
    private String cdfine30;
    @Transient
    private String remainMoney;

}
