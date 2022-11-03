package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

import java.util.List;

@ApiModel(value = "账套集合", description = "账套集合")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountVo {

    @ApiModelProperty(value = "账套代码", hidden = true)
    private String coCode;

    @ApiModelProperty(value = "账套名称", hidden = true)
    private String accNameCn;

    @ApiModelProperty(value = "账套类型", hidden = true)
    private String ztStyle;

    @ApiModelProperty(value = "默认登录账套 1 ", hidden = true)
    private String defaultLogin;

    @ApiModelProperty(value = "授权的年度", hidden = true)
    private List<String> yearList;

    @ApiModelProperty(value = "账套字母", hidden = true)
    private String accId;
}