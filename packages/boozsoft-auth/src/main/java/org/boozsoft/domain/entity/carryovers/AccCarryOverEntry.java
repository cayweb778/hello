package org.boozsoft.domain.entity.carryovers;

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

/**
 * @Author Jesse
 * @Description 结转分录 实体类
 * @Date 2021/1/29 上午10:05
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@Table("acc_carry_over_entry" )
@ApiModel(value="结转分录",description="结转分录表")
public class AccCarryOverEntry implements java.io.Serializable {
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
    private String direction;               // 方向 0余额 借方1 贷方2
    @ApiModelProperty(value = "公式", hidden = true)
    private String amountFormula;           // 公式
    @ApiModelProperty(value = "百分比", hidden = true)
    private String percentage;              // 百分比
    @ApiModelProperty(value = "公式内容json", hidden = true)
    private String entryFormulas;           // 年月 202001

    @Transient
    private BigDecimal md;
    @Transient
    private BigDecimal mc;

    public AccCarryOverEntry() {
    }
}
