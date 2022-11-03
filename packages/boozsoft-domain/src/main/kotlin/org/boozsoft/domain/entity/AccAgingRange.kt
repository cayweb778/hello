package org.boozsoft.domain.entity

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@ApiModel(value = "数据表：账龄区间表", description = "账龄区间表")
@Table("acc_aging_range")
class AccAgingRange {
    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer::class)
    private val id: String? = null

    @ApiModelProperty(value = "账龄种类：客户(CUSTOMER)、供应商(SUPPLIER)与个人往来（PERSONAL）", hidden = true)
    private val agingModel: String? = null

    @ApiModelProperty(value = "所属账套", hidden = true)
    private val accId: String? = null

    @ApiModelProperty(value = "起止天数", hidden = true)
    private val startDayNumber: String? = null

    @ApiModelProperty(value = "总天数", hidden = true)
    private val totalDayNumber: String? = null
    private val beiyong1: String? = null

    @ApiModelProperty(value = "", hidden = true)
    private val beiyong2: String? = null

    @ApiModelProperty(value = "", hidden = true)
    private val beiyong3: String? = null
}