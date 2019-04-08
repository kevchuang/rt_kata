#!/usr/bin/env bash
set e

psql -U postgres <<-EOSQL
CREATE DATABASE "kata"
  WITH OWNER = postgres
       ENCODING = 'UTF8'
       CONNECTION LIMIT = -1;
EOSQL

psql -q -U postgres -d kata <<-EOSQL
CREATE TABLE public."account" (
  account_id SERIAL NOT NULL,
  balance bigint NOT NULL DEFAULT 0,
  name TEXT NOT NULL,
  CONSTRAINT account_id PRIMARY KEY (account_id)
)
WITH (
  OIDS = FALSE
);
ALTER TABLE public."account"
  OWNER TO postgres;
CREATE TABLE public."operation" (
  operation_id SERIAL NOT NULL,
  date timestamp without time zone NOT NULL,
  account_id SERIAL NOT NULL,
  amount bigint NOT NULL,
  balance bigint NOT NULL,
  operation_type TEXT NOT NULL
)
WITH (
  OIDS = FALSE
);
ALTER TABLE public."operation"
  OWNER TO postgres;
CREATE OR REPLACE FUNCTION public.insert_operation()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    VOLATILE
    COST 100
AS \$BODY\$
BEGIN
	IF OLD.balance > NEW.balance THEN
		INSERT INTO operation (date, account_id, amount, balance, operation_type)
		VALUES (now(), NEW.account_id, OLD.balance - NEW.balance, NEW.balance, 'withdrawal');
	ELSE
		INSERT INTO operation (date, account_id, amount, balance, operation_type)
		VALUES (now(), NEW.account_id, NEW.balance - OLD.balance, NEW.balance, 'deposit');
	END IF;
	RETURN NEW;
END;
\$BODY\$;
CREATE TRIGGER last_operation
    BEFORE UPDATE
    ON public.account
    FOR EACH ROW
    EXECUTE PROCEDURE public.insert_operation();
EOSQL