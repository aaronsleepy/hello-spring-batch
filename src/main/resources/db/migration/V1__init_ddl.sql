-- customer
create table customer
(
    id    bigint auto_increment primary key comment 'ID',
    name  varchar(255) not null comment '이름',
    age   smallint comment '나이',
    type  varchar(10)  comment '유형',
    created_at datetime(6) not null comment '생성 일시',
    updated_at datetime(6) not null comment '수정 일시'
) comment '고객' charset = utf8mb4;

insert into customer(name, age, type, created_at, updated_at)
values(
          'customer#1', 23, 'seller', now(), now());

insert into customer(name, age, type, created_at, updated_at)
values(
          'customer#2', 35, 'buyer', now(), now());

insert into customer(name, age, type, created_at, updated_at)
values(
          'customer#3', 23, 'seller', now(), now());

insert into customer(name, age, type, created_at, updated_at)
values(
          'customer#4', 45, 'buyer', now(), now());

insert into customer(name, age, type, created_at, updated_at)
values(
          'customer#5', 21, 'buyer', now(), now());

insert into customer(name, age, type, created_at, updated_at)
values(
          'customer#6', 53, 'seller', now(), now());

insert into customer(name, age, type, created_at, updated_at)
values(
          'customer#7', 43, 'buyer', now(), now());

insert into customer(name, age, type, created_at, updated_at)
values(
          'customer#8', 67, 'seller', now(), now());

insert into customer(name, age, type, created_at, updated_at)
values(
          'customer#9', 12, 'buyer', now(), now());

insert into customer(name, age, type, created_at, updated_at)
values(
          'customer#10', 29, 'seller', now(), now());

insert into customer(name, age, type, created_at, updated_at)
values(
          'customer#11', 31, 'buyer', now(), now());

insert into customer(name, age, type, created_at, updated_at)
values(
          'customer#12', 85, 'buyer', now(), now());

insert into customer(name, age, type, created_at, updated_at)
values(
          'customer#13', 27, 'seller', now(), now());