package org.boozsoft.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Jesse
 * @Description 结转分录 实体类
 * @Date 2021/1/29 上午10:05
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@ApiModel(value="结转分录",description="结转分录表")
public class AccCarryOverEntryVo implements java.io.Serializable {
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "所属结转", hidden = true)
    private String onlyNum;                //分录标识
    @ApiModelProperty(value = "分录标识", hidden = true)
    private String ownership;                //分录标识
    @ApiModelProperty(value = "摘要", hidden = true)
    private String summary;                 // 摘要
    @ApiModelProperty(value = "会计科目编码", hidden = true)
    private String accountSubjectNum;       // 会计科目编码
    @ApiModelProperty(value = "会计科目名称", hidden = true)
    private String accountSubjectName;      // 会计科目名称
    @ApiModelProperty(value = "辅助核算编码", hidden = true)
    private String auxiliaryAccountingNum;  // 辅助核算编码
    @ApiModelProperty(value = "辅助核算名称", hidden = true)
    private String auxiliaryAccountingName; // 辅助核算名称
    @ApiModelProperty(value = "转账方式", hidden = true)
    private String transferMethod;          // 转账方式 转入 1 转出 2
    @ApiModelProperty(value = "方向", hidden = true)
    private String direction;               // 方向 0余额 借方余额1 贷方余额2 借方本期发生3 贷方本期发生4 借方累计发生5 贷方累计发生6
    @ApiModelProperty(value = "公式", hidden = true)
    private String amountFormula;           // 公式
    @ApiModelProperty(value = "百分比", hidden = true)
    private String percentage;              // 百分比
    @ApiModelProperty(value = "公式内容json", hidden = true)
    private String entryFormulas;           // 年月 202001

    private BigDecimal md;
    private BigDecimal mc;

    private List<KeMuBalanceVo> yuemx;// 主要是 下级科目余额
    private List<SubjectInitialBalanceVo> fuzhumx;// 主要是 下级科目辅助余额
    public AccCarryOverEntryVo() {
    }
}
