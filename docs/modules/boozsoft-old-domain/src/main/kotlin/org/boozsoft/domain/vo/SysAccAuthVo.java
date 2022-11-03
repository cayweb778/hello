package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.group.GroupSysAccAuth;

/**
 * 视图实体类
 *
 * @author Chill
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@ApiModel(value = "SysAccAuthVo对象", description = "SysAccAuthVo对象")
public class SysAccAuthVo extends GroupSysAccAuth {

    private String codeListJson;

    private String voucherTypeJson;

}
