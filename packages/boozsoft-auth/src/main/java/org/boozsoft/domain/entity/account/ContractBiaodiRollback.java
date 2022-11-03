package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：合同标的回滚表", description = "合同标的回滚表")
@Table("contract_biaodi_rollback")
@Data
public class ContractBiaodiRollback {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "合同表ID", hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long conId;
    @ApiModelProperty(value = "数量", hidden = true)
    private String biaodiCount;
    @ApiModelProperty(value = "质量", hidden = true)
    private String biaodiQuality;
    @ApiModelProperty(value = "价格", hidden = true)
    private String biaodiPrice;
    @ApiModelProperty(value = "条款", hidden = true)
    private String tiaokuan;
    @ApiModelProperty(value = "违约责任", hidden = true)
    private String zeren;
    @ApiModelProperty(value = "履行期限", hidden = true)
    private String transTerm;
    @ApiModelProperty(value = "履行地点", hidden = true)
    private String transAddress;
    @ApiModelProperty(value = "履行方式", hidden = true)
    private String transType;
    @ApiModelProperty(value = "解决方案", hidden = true)
    private String solution;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong1;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong2;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong3;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong4;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong5;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong6;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong7;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong8;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong9;
    @ApiModelProperty(value = "", hidden = true)
    private String beiyong10;
    @ApiModelProperty(value = "变动日期", hidden = true)
    private String biandongDate;
    @ApiModelProperty(value = "变动方式(1.修改;2.删除;3.停用)", hidden = true)
    private String biandongMethod;
    @ApiModelProperty(value = "操作员姓名", hidden = true)
    private String biandongName;
    @ApiModelProperty(value = "操作员唯一标识", hidden = true)
    private String biandongUniqueCode;
    @ApiModelProperty(value = "变动id", hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private String biandongId;

}
