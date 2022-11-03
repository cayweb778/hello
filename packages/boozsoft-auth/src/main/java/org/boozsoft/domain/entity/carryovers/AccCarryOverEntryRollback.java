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
 * @Description 结转分录 实体类
 * @Date 2021/1/29 上午10:05
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@Table("acc_carry_over_entry_rollback" )
@ApiModel(value="结转分录",description="结转分录回滚表")
public class AccCarryOverEntryRollback implements java.io.Serializable {
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "所属结转", hidden = true)
    private String onlyNum;                //分录标识
    @ApiModelProperty(value = "所属结转标识", hidden = true)
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
    @ApiModelProperty(value = "变动日期", hidden = true)
    private String biandongDate;
    @ApiModelProperty(value = "变动方式(1.修改;2.删除;3.停用)", hidden = true)
    private String biandongMethod;
    @ApiModelProperty(value = "操作员姓名", hidden = true)
    private String biandongName;
    @ApiModelProperty(value = "操作员唯一标识", hidden = true)
    private String biandongUniqueCode;
    public AccCarryOverEntryRollback() {
    }
}
