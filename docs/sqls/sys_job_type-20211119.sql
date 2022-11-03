-- auto-generated definition
create table sys_job_type
(
    id           varchar not null
        constraint sys_job_type_pk
            primary key,
    ec_code varchar,
    ec_name      varchar,
    unique_code_user     varchar,
    create_date     varchar,
    flag     varchar,
    tenant_id varchar default CURRENT_USER
);

comment on table sys_job_type is '职务类别表';
comment on column sys_job_type.id is '主键';
comment on column sys_job_type.ec_code is '编码';
comment on column sys_job_type.ec_name is '名称';
comment on column sys_job_type.unique_code_user is '创建人';
comment on column sys_job_type.create_date is '创建日期';
comment on column sys_job_type.flag is '是否停用(1.启用;0停用)';

alter table sys_job_type
    owner to postgres;

ALTER TABLE sys_job_type ENABLE ROW LEVEL SECURITY;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "bjlyrn-001-2021";

create policy "tenent-bjlyrn-001-2021-table-sys_job_type" on sys_job_type  for all to "bjlyrn-001-2021"  using (tenant_id  = 'bjlyrn-001-2021');
