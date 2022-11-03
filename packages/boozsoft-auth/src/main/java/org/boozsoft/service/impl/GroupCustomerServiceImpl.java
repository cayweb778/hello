package org.boozsoft.service.impl;

import cn.hutool.core.util.IdUtil;
import org.boozsoft.domain.entity.*;
import org.boozsoft.domain.entity.group.GroupCustomer;
import org.boozsoft.domain.vo.CustomerVo;
import org.boozsoft.repo.*;
import org.boozsoft.service.GroupCustomerService;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;


@Service
public class GroupCustomerServiceImpl implements GroupCustomerService {
    @Autowired
    R2dbcEntityTemplate entityTemplate;
    @Autowired
    DatabaseClient client;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupCustomerRepository customerRepository;
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


    public Flux<CustomerVo> findAll() {
        return customerRepository.findAlls();
    }

    /**
     * 集团表中增加
     *
     * @param customer
     * @return
     */
    public Mono sys_save(GroupCustomer customer) {
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
     * 集团表中增加
     *
     * @return
     */
    public Mono database_save(Customer item) {
        GroupCustomer datacus=new GroupCustomer();
        datacus.setUniqueCode(IdUtil.objectId())
                .setFlag("1")
                .setSuccessState("1")
                .setCustCode(item.getCustCode())
                .setCustName(item.getCustName())
                .setCustAbbname(item.getCustAbbname())
                .setUniqueCustclass(item.getUniqueCustclass())
                .setCustSregcode(item.getCustSregcode())
                .setCustBank(item.getCustBank())
                .setCustAccount(item.getCustAccount())
                .setCustDevdate(item.getCustDevdate())
                .setUniqueCodeCcus(item.getUniqueCodeCcus())
                .setUniqueCodeSupplier(item.getUniqueCodeSupplier())
                .setIndustryclassName(item.getIndustryclassName())
                .setContacts(item.getContacts())
                .setTelephone(item.getTelephone())
                .setAddress1(item.getAddress1())
                .setAddress2(item.getAddress2())
                .setCellPhoneNum(item.getCellPhoneNum())
                .setEmail(item.getEmail())
                .setCountryName(item.getCountryName())
                .setWebsite(item.getWebsite())
                .setManageType(item.getManageType())
                .setBankArea(item.getBankArea())
                .setBankCode(item.getBankCode())
                .setProvince(item.getProvince());
        return customerRepository.save(datacus).map(o -> R.ok().setResult(o));
    }

    /**
     * 审批通过-账套表增加
     *
     * @param customer
     * @return
     */
    
    public Mono approveOkSave_database(GroupCustomer customer) {
        return customerRepository.findByUniqueCode(customer.getUniqueCode())
                .map(x->x.getId())
                .flatMap(id->{
                    return customerRepository.deleteById(id).then(Mono.just(customer));
                })
                .map(item->item)
                .flatMap(customerRepository::save)
                .map(o -> R.ok().setResult(o));
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
    public Mono updateIsAutoSaveApi(String accId, String uniqueCode, GroupCustomer customerMono) {
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
        return customerRepository.findBySuccessStateAndUniqueCode(state,unique).map(o -> R.ok().setResult(o));
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
