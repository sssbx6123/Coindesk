CREATE TABLE currency (
    code VARCHAR(10) PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);

INSERT INTO currency (code, name) VALUES ('USD', '美元');
INSERT INTO currency (code, name) VALUES ('GBP', '英鎊');
INSERT INTO currency (code, name) VALUES ('EUR', '歐元');
