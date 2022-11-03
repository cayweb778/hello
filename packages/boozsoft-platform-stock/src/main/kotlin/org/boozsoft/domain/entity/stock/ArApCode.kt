package org.boozsoft.domain.entity.stock

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import lombok.Data
import lombok.experimental.Accessors
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Table

@Data
@Table("ar_ap_code")
@Accessors(chain = true)
@ApiModel(value = "应收应付科目表", description = "应收应付科目表")
data class ArApCode(
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer::class)
    val id: String,

//    @Transient
//    @ApiModelProperty(value = "公司唯一码", hidden = true)
//    val tenantId: String,
    
    @ApiModelProperty(value = "类别唯一码", hidden = true)
    val uniqueId: String,
    
    @ApiModelProperty(value = "类型（ar应收、ap应付）", hidden = true)
    val billStyle: String,
    
    @ApiModelProperty(value = "应收应付类别", hidden = true)
    val stName: String,
    
    @ApiModelProperty(value = "科目编码", hidden = true)
    val ccode: String?,
    
    @ApiModelProperty(value = "科目名称", hidden = true)
    val ccodeName: String?,
    
    @ApiModelProperty(value = "", hidden = true)
    val cfree1: String?,
    
    @ApiModelProperty(value = "", hidden = true)
    val cfree2: String?,
    
    @ApiModelProperty(value = "", hidden = true)
    val cfree3: String?,
    
    @ApiModelProperty(value = "", hidden = true)
    val cfree4: String?,
    
    @ApiModelProperty(value = "", hidden = true)
    val cfree5: String?,
)