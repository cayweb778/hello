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
import org.springframework.data.relational.core.mapping.Table;

/**
 * @Author Jesse
 * @Description 结转分录 公式 实体类
 * @Date 2021/1/29 上午10:05
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@Table("acc_carry_over_entry_formula_rollback" )
@ApiModel(value="结转分录公式",description="结转分录公式回滚表")
public class AccCarryOverEntryFormulaRollback implements java.io.Serializable {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "所属结转分录标识", hidden = true)
    private String ownership;                // 所属结转分录标识
    @ApiModelProperty(value = "运算符号 1 +  2 -", hidden = true)
    private String calculationSign;          // 运算符号 1 +  2 -
    @ApiModelProperty(value = "会计科目编码", hidden = true)
    private String accountSubjectNum;       // 会计科目编码
    @ApiModelProperty(value = "会计科目名称", hidden = true)
    private String accountSubjectName;      // 会计科目名称
    @ApiModelProperty(value = "辅助核算编码", hidden = true)
    private String auxiliaryAccountingNum;  // 辅助核算编码
    @ApiModelProperty(value = "辅助核算名称", hidden = true)
    private String auxiliaryAccountingName; // 辅助核算名称
    @ApiModelProperty(value = "方向", hidden = true)
    private String direction;               // 方向: y余额 jy借方余额 dy贷方余额 jf借方发生 df贷方发生 qjd 取借贷差额
    @ApiModelProperty(value = "运算", hidden = true)
    private String calculation;             // 运算 * 1 / 2
    @ApiModelProperty(value = "运算数额", hidden = true)
    private String calculationValue;        // 运算数额
    @ApiModelProperty(value = "变动日期", hidden = true)
    private String biandongDate;
    @ApiModelProperty(value = "变动方式(1.修改;2.删除;3.停用)", hidden = true)
    private String biandongMethod;
    @ApiModelProperty(value = "操作员姓名", hidden = true)
    private String biandongName;
    @ApiModelProperty(value = "操作员唯一标识", hidden = true)
    private String biandongUniqueCode;

    public AccCarryOverEntryFormulaRollback() {
    }


}
