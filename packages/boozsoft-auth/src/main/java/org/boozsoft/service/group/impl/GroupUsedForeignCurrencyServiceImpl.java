package org.boozsoft.service.group.impl;

import org.boozsoft.domain.entity.UsedForeignCurrency;
import org.boozsoft.domain.entity.organize.OrgUsedForeignCurrency;
import org.boozsoft.repo.UsedForeignCurrencyRepository;
import org.boozsoft.repo.group.GroupUsedForeignCurrencyRepository;
import org.boozsoft.repo.organize.OrgUsedForeignCurrencyRepository;
import org.boozsoft.service.group.GroupUsedForeignCurrencyService;
import org.boozsoft.domain.entity.group.GroupUsedForeignCurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;

import java.util.List;


/**
 * 科目模板
 */
@Service
public class GroupUsedForeignCurrencyServiceImpl implements GroupUsedForeignCurrencyService {


    @Autowired
    GroupUsedForeignCurrencyRepository repository;

    @Autowired
    UsedForeignCurrencyRepository usedForeignCurrencyRepository;

    @Autowired
    OrgUsedForeignCurrencyRepository orgUsedForeignCurrencyRepository;

    @Override
    public Flux<GroupUsedForeignCurrency> findAll(Pageable pageable) {
        return repository.findAll();
    }


    @Override
    public Mono<GroupUsedForeignCurrency> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<GroupUsedForeignCurrency> save(@RequestParam GroupUsedForeignCurrency entity) {
        return repository.save(entity);
    }
    public Flux<GroupUsedForeignCurrency> saveList(List<GroupUsedForeignCurrency> entity) {
        return repository.saveAll(entity);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Flux<GroupUsedForeignCurrency> duplicateCheckQuery(String value, String type) {

        return type.equals("code")?repository.findAllByForeignCode(value):repository.findAllByForeignName(value);
    }

    @Override
    public Flux<UsedForeignCurrency> findAllAcc(Pageable pageable) {
        return usedForeignCurrencyRepository.findAll();
    }

    @Override
    public Mono<UsedForeignCurrency> findByIdAcc(String id) {
        return usedForeignCurrencyRepository.findFirstById(id);
    }
    @Override
    public Mono<UsedForeignCurrency> saveAcc(UsedForeignCurrency entity) {
        return usedForeignCurrencyRepository.save(entity);
    }

    public Flux<UsedForeignCurrency> saveAccList(List<UsedForeignCurrency> entity) {
        return usedForeignCurrencyRepository.saveAll(entity);
    }
    @Override
    public Mono<Void> deleteByIdAcc(String id) {
        return usedForeignCurrencyRepository.deleteById(id);
    }

    @Override
    public Flux<UsedForeignCurrency> duplicateCheckQueryAcc(String value, String type) {
        return type.equals("code")?usedForeignCurrencyRepository.findAllByForeignCode(value):usedForeignCurrencyRepository.findAllByForeignName(value);
    }

    @Override
    public Flux<OrgUsedForeignCurrency> findAvailablesOrg(String uniqueCode) {
        return orgUsedForeignCurrencyRepository.findAllByOrgUniqueCode(uniqueCode);
    }

    @Override
    public Flux<OrgUsedForeignCurrency> findAllOrg(Pageable pageable) {
        return orgUsedForeignCurrencyRepository.findAll();
    }

    @Override
    public Mono<OrgUsedForeignCurrency> findByIdOrg(String id) {
        return orgUsedForeignCurrencyRepository.findById(id);
    }

    @Override
    public Mono<OrgUsedForeignCurrency> saveOrg(OrgUsedForeignCurrency entity) {
        return orgUsedForeignCurrencyRepository.save(entity);
    }

    @Override
    public Mono<Void> deleteByIdOrg(String id) {
        return orgUsedForeignCurrencyRepository.deleteById(id);
    }

    @Override
    public Flux<OrgUsedForeignCurrency> duplicateCheckQueryOrg(String value, String type, String uniqueCode) {
        return type.equals("code")?orgUsedForeignCurrencyRepository.findAllByForeignCodeAndOrgUniqueCode(value,uniqueCode):orgUsedForeignCurrencyRepository.findAllByForeignNameAndOrgUniqueCode(value,uniqueCode);
    }

    @Override
    public Flux<UsedForeignCurrency> findAvailablesAcc() {
        return usedForeignCurrencyRepository.findAll();
    }
}