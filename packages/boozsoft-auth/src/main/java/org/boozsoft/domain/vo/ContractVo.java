package org.boozsoft.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.boozsoft.domain.entity.account.Contract;
import org.boozsoft.domain.entity.account.ContractBiaodi;

import java.util.List;

/**
 * 视图实体类
 *
 * @author Chill
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@ApiModel(value = "ContractVo对象", description = "ContractVo对象")
public class ContractVo extends Contract {

    private List<ContractBiaodi> table;

}
