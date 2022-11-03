package org.boozsoft.domain.vo.group;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.boozsoft.domain.entity.group.GroupFaAccount;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@ApiModel(value = "数据表：固定资产账", description = "固定资产账")
@Data
public class GroupFaAccountVo extends GroupFaAccount {


    @ApiModelProperty(value = "本位币名称（三位国际代码CNY）")
    private String cname;

    @ApiModelProperty(value = "折旧方法名称（取自折旧方法表）")
    private String zjname;
}


