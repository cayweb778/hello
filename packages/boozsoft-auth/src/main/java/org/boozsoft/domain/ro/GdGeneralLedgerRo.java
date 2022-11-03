package org.boozsoft.domain.ro;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 视图实体类
 *
 * @author lz
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
public class GdGeneralLedgerRo {

    private String eccode;
    private String ecname;
    private String cardunique;
    private String cdate;
    private String yuanzhi;
    private String ljzhejiu;

}
