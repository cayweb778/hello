package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：人员类别表", description = "人员类别表")
@Table("sys_psn_type")
@Data
@NoArgsConstructor
public class SysPsnType {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "唯一标识", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "类别编码（不允许重复）", hidden = true)
    private String psnTypeCode;
    @ApiModelProperty(value = "类别名称（不允许重复）", hidden = true)
    private String psnTypeName;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;

    public SysPsnType(String uniqueCode, String psnTypeCode, String psnTypeName) {
        this.uniqueCode = uniqueCode;
        this.psnTypeCode = psnTypeCode;
        this.psnTypeName = psnTypeName;
    }
}
