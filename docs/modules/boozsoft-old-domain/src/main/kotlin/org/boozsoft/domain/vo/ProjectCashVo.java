package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "现金流量基础项目Vo", description = "现金流量基础项目Vo")
@Data
public class ProjectCashVo {
    private String value;
    private String label;
    private String fx;


}
