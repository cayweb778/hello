package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.account.BaseInfo;
import org.boozsoft.domain.entity.account.BaseInfoColumn;

import java.util.List;

/**
 * 视图实体类
 *
 * @author Chill
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@ApiModel(value = "BaseInfoVo对象", description = "BaseInfoVo对象")
public class BaseInfoVo extends BaseInfo {
    private List<BaseInfoColumn> table;
}
