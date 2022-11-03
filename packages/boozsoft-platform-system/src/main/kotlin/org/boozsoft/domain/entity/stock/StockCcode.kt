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

@Table("stock_ccode")
@ApiModel(value = "凭证生单会计科目设置主表", description = "凭证生单会计科目设置主表")
data class StockCcode(
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

    @ApiModelProperty(value = "类别名称", hidden = true)
    val stName: String,

    @ApiModelProperty(value = "会计科目编码（末级会计科目）", hidden = true)
    val ccode: String?,

    @ApiModelProperty(value = "科目名称", hidden = true)
    val ccodeName: String?,

    @ApiModelProperty(value = "添加人唯一码（操作员）", hidden = true)
    val cpersoncode: String?,

    @ApiModelProperty(value = "添加时间", hidden = true)
    val cmakerTime: String?,

    @ApiModelProperty(value = "", hidden = true)
    val cfree1: String?,

    @ApiModelProperty(value = "", hidden = true)
    val cfree2: String?,

    @ApiModelProperty(value = "", hidden = true)
    val cfree3: String?,
)