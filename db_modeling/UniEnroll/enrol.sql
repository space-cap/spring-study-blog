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
DECLARE
    PROCEDURE safe_drop(p_table_name IN VARCHAR2) IS
    BEGIN
        EXECUTE IMMEDIATE 'DROP TABLE ' || p_table_name || ' CASCADE CONSTRAINTS';
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE != -942 THEN -- ORA-00942: table or view does not exist
                RAISE;
            END IF;
    END;
BEGIN
    
    safe_drop('student_status');
    safe_drop('student');
	
	safe_drop('professor_status');
    safe_drop('professor');
	
	safe_drop('major');
	
END;
/


--------------------------------------------------------------------------------------
-- Part 2. schema
--------------------------------------------------------------------------------------


-- Create tables
CREATE TABLE student (
    no        		NUMBER(10)    	PRIMARY KEY,         
    name            NVARCHAR2(20),                      
    grade           NUMBER(5),                         
    department_code NUMBER(5),                         
    major_code      NUMBER(5),                         
    phone1          VARCHAR2(20),            
    phone2          VARCHAR2(20),            
    email           VARCHAR2(100),                     
    address         NVARCHAR2(150),                     
    photo           RAW(2000),                               
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
COMMENT ON COLUMN 	student.photo 			IS '사진 2KB, BLOB 추천';
COMMENT ON COLUMN 	student.status_code 	IS '재학상태코드';


CREATE TABLE student_status (
    status_code   NUMBER(5) 	PRIMARY KEY,  
    status_name   NVARCHAR2(20) 	NOT NULL   
);

COMMENT ON TABLE  	student_status 				IS '재학상태코드';
COMMENT ON COLUMN 	student_status.status_code 	IS '재학상태코드';
COMMENT ON COLUMN 	student_status.status_name 	IS '상태내용';

INSERT INTO student_status (status_code, status_name) VALUES (0, '재학');
INSERT INTO student_status (status_code, status_name) VALUES (1, '휴학');
INSERT INTO student_status (status_code, status_name) VALUES (2, '정학');
INSERT INTO student_status (status_code, status_name) VALUES (3, '졸업');
INSERT INTO student_status (status_code, status_name) VALUES (4, '입대');

CREATE TABLE professor (
    professor_no        	NUMBER(5)       PRIMARY KEY,
    professor_name      	NVARCHAR2(20)    NOT NULL,
    dept_code           	NUMBER(5)       NOT NULL,
    course_code         	NUMBER(10)      NOT NULL,
    resource_code       	NUMBER(5),
    phone_number        	VARCHAR2(20),
    email               	VARCHAR2(100),
    address             	NVARCHAR2(150),
    photo	            	RAW(2000),
    status_code 			NUMBER(5)    	NOT NULL
);

COMMENT ON TABLE  professor 				IS '교수 정보';
COMMENT ON COLUMN professor.professor_no 	IS '교번';
COMMENT ON COLUMN professor.professor_name 	IS '이름';
COMMENT ON COLUMN professor.dept_code 		IS '학부 코드';
COMMENT ON COLUMN professor.course_code 	IS '과목 코드';
COMMENT ON COLUMN professor.resource_code 	IS '자원 코드';
COMMENT ON COLUMN professor.phone_number 	IS '연락처';
COMMENT ON COLUMN professor.email 			IS '이메일 주소';
COMMENT ON COLUMN professor.address 		IS '주소';
COMMENT ON COLUMN professor.photo			IS '사진';
COMMENT ON COLUMN professor.status_code 	IS 'professor_status 재직 상태 코드';

CREATE TABLE professor_status (
    status_code  	NUMBER(5)       PRIMARY KEY,
    status_name     NVARCHAR2(20)    NOT NULL
);

COMMENT ON TABLE  professor_status IS '교수 재직 상태';
COMMENT ON COLUMN professor_status.status_code IS '재직 상태 코드';
COMMENT ON COLUMN professor_status.status_name IS '상태 내용';

-- 샘플 데이터 삽입
INSERT INTO professor_status (status_code, status_name) VALUES (0, '재직');
INSERT INTO professor_status (status_code, status_name) VALUES (1, '휴직');
INSERT INTO professor_status (status_code, status_name) VALUES (2, '정직');



CREATE TABLE major (
    major_code      NUMBER(5)       PRIMARY KEY,
    major_name      NVARCHAR2(40)    NOT NULL
);

COMMENT ON TABLE  major 			IS '전공 정보';
COMMENT ON COLUMN major.major_code 	IS '전공 코드';
COMMENT ON COLUMN major.major_name 	IS '전공 이름';

-- 샘플 데이터 삽입
INSERT ALL
    INTO major (major_code, major_name) VALUES (10100, '국어국문학')
    INTO major (major_code, major_name) VALUES (10200, '행정학')
    INTO major (major_code, major_name) VALUES (10300, '심리학')
    INTO major (major_code, major_name) VALUES (10400, '소프트웨어공학')
SELECT 1 FROM DUAL;

















