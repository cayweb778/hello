package org.boozsoft.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table("accvoucher_temp" )
@ApiModel(value="凭证临时表",description="凭证临时表")

public class AccvoucherTemp {

    @ApiModelProperty(value = "主键id", position = 0)
    @Id
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    @ApiModelProperty(value = "用户ID", position = 1)
    private String userId;

    @ApiModelProperty(value = "租户id", position = 1)
    private String tenantId;

    @ApiModelProperty(value = "数据", position = 1)
    private String data;

    @ApiModelProperty(value = "创建时间", position = 1)
    private String createTime;

    @ApiModelProperty(value = "年度", position = 1)
    private String iyear;

}
