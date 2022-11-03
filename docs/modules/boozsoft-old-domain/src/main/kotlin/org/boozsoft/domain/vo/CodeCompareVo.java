package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@ApiModel(value = "CodeCompareVo", description = "CodeCompareVo")
public class CodeCompareVo {

    @ApiModelProperty(value = "ID", hidden = true)
    private String id;
    @ApiModelProperty(value = "年度", hidden = true)
    private String iyear;
    @ApiModelProperty(value = "来源科目编码", hidden = true)
    private String sourceCode;
    @ApiModelProperty(value = "来源科目名称", hidden = true)
    private String sourceName;
    @ApiModelProperty(value = "目标科目编码", hidden = true)
    private String targetCode;
    @ApiModelProperty(value = "目标科目名称", hidden = true)
    private String targetName;
    @ApiModelProperty(value = "唯一码,用来标记这是一组", hidden = true)
    private String sameSource;
}
