--liquibase formatted sql

--changeset runInTransaction:true failOnError:true
create table expenses(
    id bigint auto_increment primary key,
    userName varchar not null,
    description varchar not null,
    amount_integer bigint,
    amount_fraction int,
    category varchar not null,
    location varchar not null,
    account varchar not null,
    expenseTime varchar not null
);
--rollback drop table expenses
