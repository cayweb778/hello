package org.boozsoft.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


import java.io.Serializable;

/**
 * @ClassName : TbAccVoucherFields
 * @Author : Jesse
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table("_app_group_sys_voucher_import_entry" )
@ApiModel(value="凭证导入模板字段分录表",description="凭证导入模板字段表")
public class SysVoucherImportEntry implements Serializable {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    @ApiModelProperty("分录所属")
    private String uniqueCode;           // 模板唯一

    @ApiModelProperty("导出模板标识")
    private String customerFieldName;    // 客户字段呀名称

    @ApiModelProperty("系统字段编码")
    private String systemFieldNum;       // 系统字段唯一标识

    @ApiModelProperty("系统字段名称")
    private String systemFieldName;      // 系统字段唯一名称

    @ApiModelProperty("字段类型")
    private String fieldType;            // 客户字段类型 默认文本

}
