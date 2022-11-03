-- auto-generated definition
create table _app_group_fuzhu_hesuan
(
    id                    varchar not null
        constraint _app_group_fuzhu_hesuan_pk
        primary key,
    ccode                 varchar,
    cname                 varchar,
    cankao_duixiang       varchar,
    flag                  varchar,
    remarks               varchar,
    cankao_duixiang_table varchar,
    beiyong1              varchar,
    beiyong2              varchar,
    beiyong3              varchar,
    beiyong4              varchar,
    beiyong5              varchar,
    cdfine                integer,
    cankao_duixiang_key   varchar,
    cankao_duixiang_label varchar,
    cankao_duixiang_where varchar,
    cankao_duixiang_flag  varchar,
    cankao_duixiang_code  varchar
);

comment on table _app_group_fuzhu_hesuan is '辅助核算定义';

comment on column _app_group_fuzhu_hesuan.id is '主键';

comment on column _app_group_fuzhu_hesuan.ccode is '编码';

comment on column _app_group_fuzhu_hesuan.cname is '名称';

comment on column _app_group_fuzhu_hesuan.cankao_duixiang is '参考对象';

comment on column _app_group_fuzhu_hesuan.flag is '是否停用(1.启用;0停用)';

comment on column _app_group_fuzhu_hesuan.remarks is '说明';

comment on column _app_group_fuzhu_hesuan.cankao_duixiang_table is '参考对象表名';

comment on column _app_group_fuzhu_hesuan.cdfine is '对应凭证表的(cdfine)1-30';

alter table _app_group_fuzhu_hesuan
    owner to postgres;
