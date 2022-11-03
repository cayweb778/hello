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
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table("_app_group_sys_voucher_import" )
@ApiModel(value="凭证导入模板表",description="凭证导入模板表")
public class SysVoucherImport implements Serializable {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;                  // 主键
    @ApiModelProperty("模板名称")
    private String templateName;        // 模板名称
    @ApiModelProperty("模板编码")
    private String templateNumber;      // 用于排序
    @ApiModelProperty("模板类型")
    private String templateType;        // 类型

    @Transient
    private String entryList;             // 分录信息

}
