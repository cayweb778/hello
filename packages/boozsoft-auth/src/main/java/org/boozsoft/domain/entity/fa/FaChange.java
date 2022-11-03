package org.boozsoft.domain.entity.fa;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：资产卡片变动表", description = "资产卡片变动表")
@Table("fa_change")
@Data
public class FaChange {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "公司唯一码", hidden = true)
    private String uniqueCode;
    @ApiModelProperty(value = "管理代码", hidden = true)
    private String manageCode;
    @ApiModelProperty(value = "卡片唯一码", hidden = true)
    private String cardUnique;
    @ApiModelProperty(value = "变动时间（年月日时分秒）", hidden = true)
    private String cdate;
    @ApiModelProperty(value = "操作员唯一码", hidden = true)
    private String cuserId;
    @ApiModelProperty(value = "数量变动值", hidden = true)
    private String shuliang;
    @ApiModelProperty(value = "资产原值变动值", hidden = true)
    private String yuanzhi;
    @ApiModelProperty(value = "累计折旧变动值", hidden = true)
    private String ljzhejiu;
    @ApiModelProperty(value = "资产分类变动值", hidden = true)
    private String faClass;
    @ApiModelProperty(value = "拆旧方式变动值", hidden = true)
    private String zhejiuMethod;
    @ApiModelProperty(value = "净残值率变动值", hidden = true)
    private String jingcanzhilv;
    @ApiModelProperty(value = "使用年限变动值", hidden = true)
    private String life;
    @ApiModelProperty(value = "工作总量变动值", hidden = true)
    private String zongliang;
    @ApiModelProperty(value = "存放位置变动值", hidden = true)
    private String addr;
    @ApiModelProperty(value = "管理人变动值", hidden = true)
    private String userName;
    @ApiModelProperty(value = "进销税额变动值", hidden = true)
    private String jingxiangshui;
    @ApiModelProperty(value = "责任人唯一码", hidden = true)
    private String zerenUser;
    @ApiModelProperty(value = "使用状态", hidden = true)
    private String useType;
    @ApiModelProperty(value = "计提方式", hidden = true)
    private String jitiType;
    @ApiModelProperty(value = "变动年", hidden = true)
    private String iyear;
    @ApiModelProperty(value = "变动月", hidden = true)
    private String imonth;
    @ApiModelProperty(value = "月折旧率", hidden = true)
    private String yuezhejiulv;
    @ApiModelProperty(value = "月折旧额", hidden = true)
    private String yuezhejiue;
    @ApiModelProperty(value = "是否拆分", hidden = true)
    private String chaifen;
    @ApiModelProperty(value = "是否借出", hidden = true)
    private String jiechu;
    @ApiModelProperty(value = "是否减少", hidden = true)
    private String jianshao;
    @ApiModelProperty(value = "拆分借出减少日期", hidden = true)
    private String jjTime;
    @ApiModelProperty(value = "折旧到期日（隐藏字段）", hidden = true)
    private String jzdqrTime;
    @ApiModelProperty(value = "进项税率", hidden = true)
    private String jinxiangshuilv;
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
    /************************ Mr.Ye *********************/
    @ApiModelProperty(value = "变动原因", hidden = true)
    private String changeCause;
    @ApiModelProperty(value = "变动关联ID", hidden = true)
    private String superiorId;
    /************************ Mr.Ye *********************/
    @Transient
    @ApiModelProperty(value = "购买日期", hidden = true)
    private String buyTime;

}
