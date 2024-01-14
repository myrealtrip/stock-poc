INSERT INTO `stock-study`.stock (id, remain_quantity)
VALUES (1, 1000000);
INSERT INTO `stock-study`.stock (id, remain_quantity)
VALUES (2, 1000000);
INSERT INTO `stock-study`.stock (id, remain_quantity)
VALUES (3, 1000000);

INSERT INTO `stock-study`.stock_with_optimistic_lock (id, remain_quantity, version)
VALUES (1, 960121, 39879);
INSERT INTO `stock-study`.stock_with_optimistic_lock (id, remain_quantity, version)
VALUES (2, 980048, 19952);
INSERT INTO `stock-study`.stock_with_optimistic_lock (id, remain_quantity, version)
VALUES (3, 980073, 19927);
