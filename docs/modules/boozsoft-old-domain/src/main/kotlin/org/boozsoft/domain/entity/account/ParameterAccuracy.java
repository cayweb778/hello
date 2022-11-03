package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "参数精度", description = "参数精度")
@Table("parameter_accuracy")
@Data
public class ParameterAccuracy {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    @ApiModelProperty(value = "参数代码", hidden = true)
    private String paraNum;

    @ApiModelProperty(value = "参数名称", hidden = true)
    private String paraName;

    @ApiModelProperty(value = "小数位", hidden = true)
    private String decimalPlaces;

    @ApiModelProperty(value = "控制方式 1 控制 0 不控制", hidden = true)
    private String controlManner;

    @ApiModelProperty(value = "集团管控 1 管控 0 不管控", hidden = true)
    private String groupControl;

    @ApiModelProperty(value = "所属账套", hidden = true)
    private String accountId;
    @ApiModelProperty(value = "租户ID", hidden = true)
    private String tenantId;

    @ApiModelProperty(value = "整数点数位", hidden = true)
    private String beiyong1;

    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;

    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;

}
