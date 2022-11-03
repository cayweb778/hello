package org.boozsoft.domain.entity.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：合同表", description = "合同表")
@Table("contract")
@Data
public class Contract {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "合同签订日期", hidden = true)
    private String conDate;
    @ApiModelProperty(value = "合同编号", hidden = true)
    private String conNum;
    @ApiModelProperty(value = "合同生效日期", hidden = true)
    private String shengxiaoDate;
    @ApiModelProperty(value = "合同开始日期", hidden = true)
    private String startDate;
    @ApiModelProperty(value = "合同结束日期", hidden = true)
    private String endDate;
    @ApiModelProperty(value = "合同名称", hidden = true)
    private String conName;
    @ApiModelProperty(value = "合同金额", hidden = true)
    private String amount;
    @ApiModelProperty(value = "合同时候预付金额", hidden = true)
    private String firstAmount;
    @ApiModelProperty(value = "履行地点", hidden = true)
    private String transAddress;
    @ApiModelProperty(value = "履行方式", hidden = true)
    private String transType;
    @ApiModelProperty(value = "履行期限", hidden = true)
    private String transTerm;
    @ApiModelProperty(value = "是否结束(0.否；1.是)", hidden = true)
    private String status;
    @ApiModelProperty(value = "合同存放位置", hidden = true)
    private String saveAddress;
    @ApiModelProperty(value = "合同保管人", hidden = true)
    private String safekeepPsn;
    @ApiModelProperty(value = "合同甲方全称", hidden = true)
    private String jiaName;
    @ApiModelProperty(value = "合同甲方地址", hidden = true)
    private String jiaAddress;
    @ApiModelProperty(value = "合同甲方税号", hidden = true)
    private String jiaShuihao;
    @ApiModelProperty(value = "合同甲方代表人", hidden = true)
    private String jiaPerson;
    @ApiModelProperty(value = "合同甲方联系方式", hidden = true)
    private String jiaPhone;
    @ApiModelProperty(value = "合同乙方全称", hidden = true)
    private String yiName;
    @ApiModelProperty(value = "合同乙方地址", hidden = true)
    private String yiAddress;
    @ApiModelProperty(value = "合同乙方税号", hidden = true)
    private String yiShuihao;
    @ApiModelProperty(value = "合同乙方代表人", hidden = true)
    private String yiPerson;
    @ApiModelProperty(value = "合同乙方联系方式", hidden = true)
    private String yiPhone;
    @ApiModelProperty(value = "合同丙方全称", hidden = true)
    private String bingName;
    @ApiModelProperty(value = "合同丙方地址", hidden = true)
    private String bingAddress;
    @ApiModelProperty(value = "合同丙方税号", hidden = true)
    private String bingShuihao;
    @ApiModelProperty(value = "合同丙方代表人", hidden = true)
    private String bingPerson;
    @ApiModelProperty(value = "合同丙方联系方式", hidden = true)
    private String bingPhone;
    @ApiModelProperty(value = "合同丁方全称", hidden = true)
    private String dingName;
    @ApiModelProperty(value = "合同丁方地址", hidden = true)
    private String dingAddress;
    @ApiModelProperty(value = "合同丁方税号", hidden = true)
    private String dingShuihao;
    @ApiModelProperty(value = "合同丁方代表人", hidden = true)
    private String dingPerson;
    @ApiModelProperty(value = "合同丁方联系方式", hidden = true)
    private String dingPhone;
    @ApiModelProperty(value = "对应凭证号（唯一标识）", hidden = true)
    private String vouchCode;
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

}
