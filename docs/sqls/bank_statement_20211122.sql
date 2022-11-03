alter table bank_statement
    add iyear varchar;

comment on column bank_statement.iyear is '年度';