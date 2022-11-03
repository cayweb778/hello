package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "档案名：凭证摘要", description = "凭证摘要")
@Table("accvoucher_cdigest")
@Data
public class AccountAccvoucherCdigest {

    @Id
    @ApiModelProperty(value = "主键", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    @ApiModelProperty(value = "编码", hidden = true)
    private String ccode;


    @ApiModelProperty(value = "常用摘要内容", hidden = true)
    private String content;


    @ApiModelProperty(value = "所属分类", hidden = true)
    private String classCode;

}
