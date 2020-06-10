create table category (
    id uuid not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    name varchar(255),
    primary key (id));

create table vehicle_categories (
    vehicle_id uuid not null,
    category_id uuid not null,
    primary key (vehicle_id, category_id));

create table vehicles (
    id uuid not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    description varchar(255) not null,
    engine_number int8 not null check (engine_number<=99999 AND engine_number>=10000),
    images text[],
    name varchar(255) not null,
    tags text[],
    primary key (id));

alter table category add constraint UK_46ccwnsi9409t36lurvtyljak unique (name);
alter table vehicles add constraint UK_3jtis68l3n7tt31ob3apate19 unique (engine_number);
alter table vehicles add constraint UK_t1k36pka00ofbffkfe7ahqya7 unique (name);
alter table vehicle_categories add constraint FKhhqvwe05cikqmpgemqj2smfnm foreign key (category_id) references category;
alter table vehicle_categories add constraint FKda3bwxiknkea5j8185p9w8n0p foreign key (vehicle_id) references vehicles;
