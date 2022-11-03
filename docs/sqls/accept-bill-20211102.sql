alter table accept_bill
    add icheck varchar;

comment on column accept_bill.icheck is '审核状态（1.已审核）';

alter table accept_bill
    add icheck_date varchar;

comment on column accept_bill.icheck_date is '审核时间';

alter table accept_bill
    add icheck_user varchar;

comment on column accept_bill.icheck_user is '审核人';

alter table accept_bill
    add qichu varchar;

comment on column accept_bill.qichu is '是否期初（1.是期初）';