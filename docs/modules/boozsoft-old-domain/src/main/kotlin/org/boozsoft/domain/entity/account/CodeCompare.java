package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：科目对照表", description = "科目对照表")
@Table("code_compare")
@Data
public class CodeCompare {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "年度", hidden = true)
    private String iyear;
    @ApiModelProperty(value = "来源科目编码", hidden = true)
    private String sourceCode;
    @ApiModelProperty(value = "目标科目编码", hidden = true)
    private String targetCode;
    @ApiModelProperty(value = "唯一码,用来标记这是一组", hidden = true)
    private String sameSource;

}
