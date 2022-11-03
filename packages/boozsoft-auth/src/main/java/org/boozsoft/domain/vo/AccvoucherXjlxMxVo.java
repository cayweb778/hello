package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.Accvoucher;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class AccvoucherXjlxMxVo{

    @ApiModelProperty(value = "序号", position = 1)
    private String number;
    @ApiModelProperty(value = "现金流量分类名称", position = 2)
    private String xjtypename;
    @ApiModelProperty(value = "现金流量编码", position = 2)
    private String xjcode;
    @ApiModelProperty(value = "现金流量项目", position = 3)
    private String xjname;
    @ApiModelProperty(value = "凭证日期", position = 4)
    private String dbillDate;
    @ApiModelProperty(value = "凭证号", position = 5)
    private String inoId;
    @ApiModelProperty(value = "凭证摘要", position = 6)
    private String cdigest;
    private String fx;
    @ApiModelProperty(value = "'余额", position = 7)
    private String money;
    @ApiModelProperty(value = "'余额", position = 7)
    private String md;
    @ApiModelProperty(value = "'余额", position = 7)
    private String mc;

}
