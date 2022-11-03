package org.boozsoft.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

@ApiModel(value = "数据表：账套年度模式对照表", description = "账套年度模式对照表")
@Data
public class SysAccountPeriodVo {
    private String id;
    private String accountId;
    private String accountYear;
    private String accountMode;
    private String jici;
    private Integer jiciLength;
    private String accName;
    private String databaseId;
}
