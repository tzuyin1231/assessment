CREATE DATABASE assessment ENCODING UTF8;
\c assessment;
create table users(
    userId serial not null primary key ,
    nickname varchar(20) not null unique key,
    phone varchar(10) not null unique key,
    password varchar(60) not null
);
create table articles(
    articleId serial not null primary key ,
    userId int(5) not null,
    articleTitle varchar(50) not null unique key,
    articleContent varchar(4000),
    status varchar(1) not null default '1',
    createTime timestamp default current_timestamp,
    updateTime timestamp default current_timestamp
)