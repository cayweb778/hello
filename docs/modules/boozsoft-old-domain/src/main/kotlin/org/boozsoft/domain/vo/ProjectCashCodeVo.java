package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.share.ProjectCash;
import org.boozsoft.domain.entity.share.ProjectCashCode;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="现金流量项目",description="现金流量项目")
public class ProjectCashCodeVo extends ProjectCash {
    @ApiModelProperty(value = "对应借方科目编码（末级科目）", hidden = true)
    private String jcode;
    @ApiModelProperty(value = "对应贷方科目编码（末级科目）", hidden = true)
    private String dcode;
}
