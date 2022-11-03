package org.boozsoft.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.Accvoucher;
import org.boozsoft.domain.entity.CashFlow;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "现金流量Vo", description = "现金流量Vo")
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
public class CashFlowVo extends CashFlow {

    @ApiModelProperty(value = "序号")
    private String num;
    @ApiModelProperty(value = "对应序号")
    private String dnum;
    @ApiModelProperty(value = "现金流量科目")
    private String codename;
    @ApiModelProperty(value = "摘要")
    private String cdigest;
    @ApiModelProperty(value = "对方科目")
    private String dcodename;
    @ApiModelProperty(value = "流量编码")
    private String projectnum;
    @ApiModelProperty(value = "现金流量项目")
    private String projectname;
    @ApiModelProperty(value = "资金方向", position = 1)
    private String fx;

}
