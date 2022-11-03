package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 固定资产科目设置
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class FaKmGdjzVo {

    @ApiModelProperty(value = "id", hidden = true)
    private String id;

    @ApiModelProperty(value = "编码", hidden = true)
    private String ecode;

    @ApiModelProperty(value = "名称", hidden = true)
    private String ename;

    @ApiModelProperty(value = "科目编码", hidden = true)
    private String kmcode;

    @ApiModelProperty(value = "科目名称", hidden = true)
    private String kmname;

    @ApiModelProperty(value = "类型 0固定资产、1累计折旧、2部门、3项目", hidden = true)
    private String flg;
}
