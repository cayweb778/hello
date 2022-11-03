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

@Table("stock_ccodes")
@ApiModel(value = "凭证生单会计科目设置子表", description = "凭证生单会计科目设置子表")
data class StockCcodes(
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

    @ApiModelProperty(value = "存货分类编码", hidden = true)
    val stName: String,

    @ApiModelProperty(value = "仓库唯一码", hidden = true)
    val ckName: String,

    @ApiModelProperty(value = "会计科目编码（末级会计科目）", hidden = true)
    val ccode: String,

    @ApiModelProperty(value = "科目名称", hidden = true)
    val ccodeName: String,

    @ApiModelProperty(value = "", hidden = true)
    val field1: String?,

    @ApiModelProperty(value = "", hidden = true)
    val field2: String?,

    @ApiModelProperty(value = "", hidden = true)
    val field3: String?,

    @ApiModelProperty(value = "", hidden = true)
    val field4: String?,

    @ApiModelProperty(value = "", hidden = true)
    val field5: String?,

    @ApiModelProperty(value = "", hidden = true)
    val field6: String?,

    @ApiModelProperty(value = "", hidden = true)
    val field7: String?,

    @ApiModelProperty(value = "", hidden = true)
    val field8: String?,

    @ApiModelProperty(value = "", hidden = true)
    val field9: String?,

    @ApiModelProperty(value = "", hidden = true)
    val field10: String?,

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

    @ApiModelProperty(value = "", hidden = true)
    val cfree6: String?,

    @ApiModelProperty(value = "", hidden = true)
    val cfree7: String?,

    @ApiModelProperty(value = "", hidden = true)
    val cfree8: String?,

    @ApiModelProperty(value = "", hidden = true)
    val cfree9: String?,

    @ApiModelProperty(value = "", hidden = true)
    val cfree10: String?,
)