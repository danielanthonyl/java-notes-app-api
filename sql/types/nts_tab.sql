CREATE OR REPLACE TYPE nt_row AS OBJECT (
    id VARCHAR(36),
    title VARCHAR(100),
    body VARCHAR(1000),
);

CREATE OR REPLACE TYPE nts_tab AS TABLE OF nt_row;