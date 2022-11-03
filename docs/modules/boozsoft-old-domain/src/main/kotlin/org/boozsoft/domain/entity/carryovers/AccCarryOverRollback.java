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
 * @Description 结转 实体类
 * @Date 2021/1/29 上午10:05
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@Table("acc_carry_over_rollback" )
@ApiModel(value="结转回滚",description="结转回滚表")
public class AccCarryOverRollback implements java.io.Serializable {
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "编码", hidden = true)
    private String templateNum;	    // 编码
    @ApiModelProperty(value = "名称", hidden = true)
    private String templateName;	// 名称
    @ApiModelProperty(value = "结转类型 公式：1 对应 2", hidden = true)
    private String carryOverType;	// 结转类型 公式：1 对应 2
    @ApiModelProperty(value = "系统：1 自定义：2 对应", hidden = true)
    private String carryOverModule; // 系统：1 自定义：2 对应
    @ApiModelProperty(value = "排序编码", hidden = true)
    private String orderNum;
    @ApiModelProperty(value = "会计科目类型", hidden = true)
    private String kmtype;          // 会计科目类型
    @ApiModelProperty(value = "变动日期", hidden = true)
    private String biandongDate;
    @ApiModelProperty(value = "变动方式(1.修改;2.删除;3.停用)", hidden = true)
    private String biandongMethod;
    @ApiModelProperty(value = "操作员姓名", hidden = true)
    private String biandongName;
    @ApiModelProperty(value = "操作员唯一标识", hidden = true)
    private String biandongUniqueCode;
    public AccCarryOverRollback() {
    }
}
