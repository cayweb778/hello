package org.boozsoft.domain.entity.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：账套年度模式对照表", description = "账套年度模式对照表")
@Table("_app_group_sys_account_period")
@Data
public class SysAccountPeriod {

    @Id
    @ApiModelProperty(value = "ID", hidden = true)
    @CreatedBy
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    @ApiModelProperty(value = "公司代码", hidden = true)
    private String corpCoCode;
    @ApiModelProperty(value = "账套代码(财务、资产、存货)", hidden = true)
    private String accountCoCode;
    @ApiModelProperty(value = "账套(财务、资产、存货)", hidden = true)
    private String accountId;
    @ApiModelProperty(value = "账套年度", hidden = true)
    private String accountYear;
    @ApiModelProperty(value = "模式名称", hidden = true)
    private String accountMode;
    @ApiModelProperty(value = "科目级次", hidden = true)
    private String jici;
    @ApiModelProperty(value = "级次长度", hidden = true)
    private Integer jiciLength;

    private String beiyong1;

    private String beiyong2;

    private String beiyong3;

    private String beiyong4;

    private String beiyong5;
    public SysAccountPeriod(){};
    public SysAccountPeriod(String accountId, String accountYear, String accountMode, String jici, Integer jiciLength) {
        this.accountId = accountId;
        this.accountYear = accountYear;
        this.accountMode = accountMode;
        this.jici = jici;
        this.jiciLength = jiciLength;
    }

    public SysAccountPeriod(String corpCoCode, String accountCoCode, String accountId, String accountYear, String accountMode, String jici, Integer jiciLength) {
        this.corpCoCode = corpCoCode;
        this.accountCoCode = accountCoCode;
        this.accountId = accountId;
        this.accountYear = accountYear;
        this.accountMode = accountMode;
        this.jici = jici;
        this.jiciLength = jiciLength;
    }
}
