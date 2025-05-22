--------------------------------------------------------------------------------------
-- Name	       : 이영현
-- Link	       : 
-- Version     : 0.1
-- Last Updated: 2025-05-22
-- Copyright   : Copyright © 2025 by www.saju8.com. All Rights Reserved.
-- Notice      : son학교 수강신청 샘플 디비.
--               
--               
--------------------------------------------------------------------------------------
SET DEFINE OFF;
--------------------------------------------------------------------------------------
-- Part 1. drop existing tables
--------------------------------------------------------------------------------------
create or replace PROCEDURE drop_table_if_exists (p_table_name VARCHAR2) 
IS
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE ' || p_table_name || ' cascade constraints';
EXCEPTION
  WHEN OTHERS THEN
    IF SQLCODE != -942 THEN
      RAISE;
    END IF;
END;
/

BEGIN
  drop_table_if_exists('student');
  drop_table_if_exists('student_status');
  
  
END;
/

--------------------------------------------------------------------------------------
-- Part 2. schema
--------------------------------------------------------------------------------------


-- Create tables
CREATE TABLE student (
    no        		NUMBER(10)    	PRIMARY KEY,         
    name            VARCHAR2(20),                      
    grade           NUMBER(5),                         
    department_code NUMBER(5),                         
    major_code      NUMBER(5),                         
    phone1          VARCHAR2(20) 	UNIQUE,            
    phone2          VARCHAR2(20) 	UNIQUE,            
    email           VARCHAR2(100),                     
    address         VARCHAR2(150),                     
    photo           RAW,                               
    status_code     NUMBER(5)                          
);

COMMENT ON TABLE 	student 				IS '학생 정보';
COMMENT ON COLUMN 	student.no 				IS '학번';
COMMENT ON COLUMN 	student.name 			IS '이름';
COMMENT ON COLUMN 	student.grade 			IS '학년';
COMMENT ON COLUMN 	student.department_code IS '학부코드';
COMMENT ON COLUMN 	student.major_code 		IS '전공코드';
COMMENT ON COLUMN 	student.phone1 			IS '연락처1';
COMMENT ON COLUMN 	student.phone2 			IS '연락처2';
COMMENT ON COLUMN 	student.email 			IS '이메일';
COMMENT ON COLUMN 	student.address 		IS '주소';
COMMENT ON COLUMN 	student.photo 			IS '사진';
COMMENT ON COLUMN 	student.status_code 	IS '재학상태코드';


CREATE TABLE student_status (
    status_code   NUMBER(5) 	PRIMARY KEY,  
    status_name   VARCHAR2(20) 	NOT NULL   
);

COMMENT ON TABLE  	student_status 				IS '재학상태코드';
COMMENT ON COLUMN 	student_status.status_code 	IS '재학상태코드';
COMMENT ON COLUMN 	student_status.status_name 	IS '상태내용';

ALTER TABLE student
ADD CONSTRAINT fk_student_status
FOREIGN KEY (status_code)
REFERENCES student_status(status_code);
















INSERT INTO student_status (status_code, status_name) VALUES (0, '재학');
INSERT INTO student_status (status_code, status_name) VALUES (1, '휴학');
INSERT INTO student_status (status_code, status_name) VALUES (2, '정학');
INSERT INTO student_status (status_code, status_name) VALUES (3, '졸업');
INSERT INTO student_status (status_code, status_name) VALUES (4, '입대');








