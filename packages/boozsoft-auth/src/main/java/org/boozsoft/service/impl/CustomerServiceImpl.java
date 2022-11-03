package org.boozsoft.service.impl;

import org.boozsoft.domain.entity.*;
import org.boozsoft.domain.vo.CustomerVo;
import org.boozsoft.repo.*;
import org.boozsoft.service.CustomerService;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author LZ
 * @version 1.0
 * @title 操作人员
 * @company 财税达软件科技
 * @date 2021/4/1 10:47 上午
 */
@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    R2dbcEntityTemplate entityTemplate;
    @Autowired
    DatabaseClient client;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    GroupDistRepository groupDistRepository;
    @Autowired
    SysLogRepository sysLogRepository;
    @Autowired
    SysDistReocrdRepository sysDistReocrdRepository;
    @Autowired
    SysNoticeRepository sysNoticeRepository;
    @Autowired
    CustomerRollBackRepository customerRollBackRepository;


    public Flux<CustomerVo> findAllCustomerByDatabase(String test) {
        if (StringUtils.isBlank(test) || "0".equals(test)) {
            // 查询集团表是否有本账套未生效的单据,并且查询账套客户信息
            return customerRepository.findAllByApplyDatabaseUniqueCodeAndApplyUserAndSuccessState();
        } else {
            return customerRepository.findAllByUniqueCustclass(test);
        }
    }

    public Mono sys_save(Customer customer) {
        return customerRepository.save(customer).map(o -> R.ok().setResult(o));
    }

    /**
     * 系统日志表增加
     *
     * @return
     */
    
    public Mono syslog_save(String text) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return sysLogRepository.save(
                new SysLog().setOperationDate(time)
                        .setUniqueCode("a001")
                        .setUserName("test")
                        .setAccId("bjxgkj-001")
                        .setIyear("2021")
                        .setOperationCont(text)
        ).map(o -> R.ok().setResult(o));
    }

    /**
     * 集团档案分配表
     *
     * @param databaseUniqueCode 账套唯一码
     * @param recordUniqueCode   档案唯一码
     * @param recordType         档案类型
     * @return
     */
    public Mono sysDistRecord_save(String databaseUniqueCode, String recordUniqueCode, String recordType) {
        return  sysDistReocrdRepository.countByDatabaseUniqueCodeAndRecordUniqueCodeAndRecordType(databaseUniqueCode,recordUniqueCode,recordType)
                .map(item->item)
                .flatMap(item2->{
                    if(item2==0){
                        return sysDistReocrdRepository.save(new SysDistRecord().setRecordType(recordType).setRecordUniqueCode(recordUniqueCode).setDatabaseUniqueCode(databaseUniqueCode))
                                .map(o -> Mono.just(o));
                    }
                    return Mono.just("");
                })
                .map(o -> R.ok().setResult(o));
    }

    /**
     * 账套表中增加
     *
     * @param customerMono
     * @return
     */
    
    public Mono database_save(Customer customerMono) {
        return customerRepository.save(customerMono).map(o -> R.ok().setResult(o));
    }


    /**
     * 修改状态：开启 自动分配（集团/已经下发过的账套 全更新）
     *
     * @param accId        账套编码
     * @param uniqueCode   客户唯一码
     * @param customerMono 客户实体类
     * @return
     */
    @Override
    public Mono updateIsAutoSaveApi(String accId, String uniqueCode, Customer customerMono) {
        System.out.println("暂缓开发，动态切换数据库没实现");
        System.out.println(accId + ">>>");
        return Mono.just(R.ok());
    }

    /**
     * 系统通知信息表
     * 未获取session中数据【写死】
     *
     * @return
     */
    public Mono sysnotice_save() {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return sysNoticeRepository.save(
                new SysNotice()
                        .setCorpUniqueCode("abc001")
                        .setUserUniqueCode("user001")
                        .setNoticeType("审核")
                        .setNoticeTime(time)
                        .setNoticeContent("你申请的数据已审批通过，请查收！")
                        .setReceiveRole("001")
                        .setReceiveUser("test")
                        .setNoticeState("0")
        ).map(o -> R.ok().setResult(o));
    }


    public Mono findBySuccessStateAndUniqueCode(String state, String unique) {
        return customerRepository.findByUniqueCode(unique).map(o -> R.ok().setResult(o));
    }

    /**
     * 根据ID删除系统客户信息
     * @param id
     * @return
     */

    public Mono delFindById(String id) {
        return customerRepository.deleteById(id).then();
    }
}
