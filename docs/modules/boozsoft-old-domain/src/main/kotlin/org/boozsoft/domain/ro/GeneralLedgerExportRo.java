package org.boozsoft.domain.ro;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.boozsoft.domain.vo.GeneralLedgerVo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 总账导出参数
 *
 * @author lz
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@ApiModel(value = "总 账导出参数", description = "总账导出参数")
public class GeneralLedgerExportRo {
    private  String kmName;
    private  String km;
    private  String qj;
    private  String bz;
    List<GeneralLedgerRo> rlList =  new ArrayList<>();
    /*private Integer number;
    private  String num;
    private  String name;
    private  String fx;
    private  String dw;
    private  String wb;
    private  String sjnum;
    private  String sjmoney;
    private  String  sjwb;
    private  String sdnum;
    private  String sdmoney;
    private  String sdwb;
    private  String synum;
    private  String symoney;
    private  String sywb;*/
}
