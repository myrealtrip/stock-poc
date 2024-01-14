create table stock
(
    id              bigint not null
        primary key,
    remain_quantity bigint null
);

INSERT INTO `stock-study`.stock (id, remain_quantity)
VALUES (1, 1000000);
INSERT INTO `stock-study`.stock (id, remain_quantity)
VALUES (2, 1000000);
INSERT INTO `stock-study`.stock (id, remain_quantity)
VALUES (3, 1000000);

create table stock_seq
(
    next_val bigint null
);

INSERT INTO `stock-study`.stock_seq (next_val)
VALUES (1);

create table stock_with_optimistic_lock
(
    id              bigint not null
        primary key,
    remain_quantity bigint null,
    version         int    not null
);

INSERT INTO `stock-study`.stock_with_optimistic_lock (id, remain_quantity, version)
VALUES (1, 1000000, 0);
INSERT INTO `stock-study`.stock_with_optimistic_lock (id, remain_quantity, version)
VALUES (2, 1000000, 0);
INSERT INTO `stock-study`.stock_with_optimistic_lock (id, remain_quantity, version)
VALUES (3, 1000000, 0);

create table stock_with_optimistic_lock_seq
(
    next_val bigint null
);

INSERT INTO `stock-study`.stock_with_optimistic_lock_seq (next_val)
VALUES (1);
