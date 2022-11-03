package org.boozsoft.service;

import org.boozsoft.domain.entity.group.Bank;
import reactor.core.publisher.Mono;

public interface BankService {

    /** 系统银行档案 */
    Mono sys_save(Bank bank);
    /** 系统银行档案 */
    Mono acc_save(Bank bank);

}
