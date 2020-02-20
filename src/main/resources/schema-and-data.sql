CREATE TABLE IF NOT EXISTS account
(
    id      IDENTITY PRIMARY KEY,
    balance DECIMAL(18, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS transfer
(
    id          IDENTITY PRIMARY KEY,
    sender_id   LONG           NOT NULL,
    receiver_id LONG           NOT NULL,
    amount      DECIMAL(18, 2) NOT NULL
);

INSERT INTO account(balance)
values (20.00);
INSERT INTO account(balance)
values (400.00);
INSERT INTO account(balance)
values (3000.00);
INSERT INTO account(balance)
values (500.00);
