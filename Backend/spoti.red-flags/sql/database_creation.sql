create sequence cards_id_seq;

create table "Packs"
(
    id             integer     not null
        primary key,
    name           varchar(50) not null
        unique,
    positive_count integer,
    negative_count integer
);

create table "Cards"
(
    id      integer default nextval('cards_id_seq'::regclass) not null
        primary key,
    packid  integer                                           not null
        constraint "Cards_Packs_null_fk"
            references "Packs",
    type    varchar(32)                                       not null,
    content varchar(127)                                      not null
);


