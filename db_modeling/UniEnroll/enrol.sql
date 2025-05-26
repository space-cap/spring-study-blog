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
	safe_drop('department');
	safe_drop('lecture_hall');
	safe_drop('course_category');
	safe_drop('staff_category');
	
	
	safe_drop('employees');
	safe_drop('courses');
	safe_drop('lecture_rooms');
	safe_drop('subjects');
	
	
END;
/


--------------------------------------------------------------------------------------
-- Part 2. schema
--------------------------------------------------------------------------------------


-- Create tables



CREATE TABLE student_status (
    status_code   NUMBER(5) 	PRIMARY KEY,  
    status_name   NVARCHAR2(20) 	NOT NULL   
);

COMMENT ON TABLE  	student_status 				IS '학생 상태코드';
COMMENT ON COLUMN 	student_status.status_code 	IS '상태코드';
COMMENT ON COLUMN 	student_status.status_name 	IS '상태이름';

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
COMMENT ON COLUMN professor.status_code 	IS 'professor_status 상태 코드';

CREATE TABLE professor_status (
    status_code  	NUMBER(5)       PRIMARY KEY,
    status_name     NVARCHAR2(20)    NOT NULL
);

COMMENT ON TABLE  professor_status IS '교수 상태';
COMMENT ON COLUMN professor_status.status_code IS '상태 코드';
COMMENT ON COLUMN professor_status.status_name IS '상태 이름';

-- 샘플 데이터 삽입
INSERT INTO professor_status (status_code, status_name) VALUES (0, '재직');
INSERT INTO professor_status (status_code, status_name) VALUES (1, '휴직');
INSERT INTO professor_status (status_code, status_name) VALUES (2, '정직');



CREATE TABLE major (
    major_code      NUMBER(5)       PRIMARY KEY,
    major_name      NVARCHAR2(40)   NOT NULL
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


CREATE TABLE department (
    dept_code       NUMBER(5)       PRIMARY KEY,
    dept_name       NVARCHAR2(50)   NOT NULL
);

COMMENT ON TABLE  department 			IS '학부 정보';
COMMENT ON COLUMN department.dept_code 	IS '학부 코드';
COMMENT ON COLUMN department.dept_name 	IS '학부 이름';

-- 샘플 데이터 삽입 (교수 및 학생 테이블의 학부 코드를 기반으로)
INSERT ALL
    INTO department (dept_code, dept_name) VALUES (101, '인문대학')
    INTO department (dept_code, dept_name) VALUES (102, '사회과학대학')
    INTO department (dept_code, dept_name) VALUES (103, '자연과학대학')
    INTO department (dept_code, dept_name) VALUES (104, '공과대학')
SELECT 1 FROM DUAL;


CREATE TABLE lecture_hall (
    hall_code       NUMBER(5)       PRIMARY KEY,
    hall_name       NVARCHAR2(20)   NOT NULL
);

COMMENT ON TABLE  lecture_hall 				IS '강의관 정보';
COMMENT ON COLUMN lecture_hall.hall_code 	IS '강의관 코드';
COMMENT ON COLUMN lecture_hall.hall_name 	IS '강의관 이름';

-- 샘플 데이터 삽입
INSERT ALL
    INTO lecture_hall (hall_code, hall_name) VALUES (0, '윤주관')
    INTO lecture_hall (hall_code, hall_name) VALUES (1, '아이스관')
    INTO lecture_hall (hall_code, hall_name) VALUES (2, '비트캠프관')
    INTO lecture_hall (hall_code, hall_name) VALUES (3, '영광의 레이서관')
SELECT 1 FROM DUAL;



CREATE TABLE course_category (
    category_code   NUMBER(5)      PRIMARY KEY,
    category_name   NVARCHAR2(20)  NOT NULL
);

COMMENT ON TABLE  course_category 				IS '교과 구분 정보';
COMMENT ON COLUMN course_category.category_code IS '교과 구분 코드';
COMMENT ON COLUMN course_category.category_name IS '교과 구분 이름';

-- 샘플 데이터 삽입
INSERT ALL
    INTO course_category (category_code, category_name) VALUES (0, '전공필수')
    INTO course_category (category_code, category_name) VALUES (1, '전공선택')
    INTO course_category (category_code, category_name) VALUES (2, '교양필수')
    INTO course_category (category_code, category_name) VALUES (3, '교양선택')
SELECT 1 FROM DUAL;


CREATE TABLE staff_category (
    staff_code      NUMBER(5)       PRIMARY KEY,
    staff_type      NVARCHAR2(20)   NOT NULL
);

COMMENT ON TABLE  staff_category 			IS '임직원 분류 정보';
COMMENT ON COLUMN staff_category.staff_code IS '직원 코드';
COMMENT ON COLUMN staff_category.staff_type IS '직원 분류';

-- 샘플 데이터 삽입
INSERT ALL
    INTO staff_category (staff_code, staff_type) VALUES (0, '교수')
    INTO staff_category (staff_code, staff_type) VALUES (1, '부교수')
    INTO staff_category (staff_code, staff_type) VALUES (2, '강사')
    INTO staff_category (staff_code, staff_type) VALUES (3, '행정직원')
SELECT 1 FROM DUAL;



CREATE TABLE employees (
    employee_id     NUMBER(10)      PRIMARY KEY,        -- 임직원 ID
    employee_name   NVARCHAR2(50)   NOT NULL,           -- 이름
    contact         VARCHAR2(40),                       -- 연락처
    email           VARCHAR2(50)                        -- 이메일
);

COMMENT ON TABLE  employees                  IS '임직원 정보';
COMMENT ON COLUMN employees.employee_id      IS '임직원 ID';
COMMENT ON COLUMN employees.employee_name    IS '이름';
COMMENT ON COLUMN employees.contact          IS '연락처';
COMMENT ON COLUMN employees.email            IS '이메일';

-- 샘플 데이터 10개 삽입
INSERT ALL
    INTO employees (employee_id, employee_name, contact, email) VALUES (6547, '김소영', '010-6598-5467', 'fkgj@abc.com')
    INTO employees (employee_id, employee_name, contact, email) VALUES (6586, '오윤주', '010-6598-5468', 'asd@abc.com')
    INTO employees (employee_id, employee_name, contact, email) VALUES (6590, '최한성', '010-6598-5469', 'fk45@abc.com')
    INTO employees (employee_id, employee_name, contact, email) VALUES (6798, '김성은', '010-6598-5470', 'sgkf@abc.com')
    INTO employees (employee_id, employee_name, contact, email) VALUES (6801, '박지훈', '010-6598-5471', 'jhpark@abc.com')
    INTO employees (employee_id, employee_name, contact, email) VALUES (6802, '이민정', '010-6598-5472', 'mjlee@abc.com')
    INTO employees (employee_id, employee_name, contact, email) VALUES (6803, '정유진', '010-6598-5473', 'yjjeong@abc.com')
    INTO employees (employee_id, employee_name, contact, email) VALUES (6804, '한지수', '010-6598-5474', 'jshan@abc.com')
    INTO employees (employee_id, employee_name, contact, email) VALUES (6805, '최재원', '010-6598-5475', 'jwchoi@abc.com')
    INTO employees (employee_id, employee_name, contact, email) VALUES (6806, '윤하늘', '010-6598-5476', 'hnyoon@abc.com')
SELECT 1 FROM DUAL;


CREATE TABLE courses (
    course_id        NUMBER(10)      PRIMARY KEY,          -- 과정코드
    subject_id       NUMBER(10)      NOT NULL,             -- 과목코드
    course_name      NVARCHAR2(50)   NOT NULL,             -- 과정이름
    course_desc      NVARCHAR2(200),                       -- 과정 설명
    start_date       DATE,                                 -- 시작일
    end_date         DATE,                                 -- 종료일
    manager_id       NUMBER(10),                           -- 담당자 코드 (직원 ID)
    recruit_count    NUMBER(5),                            -- 모집정원
    enroll_count     NUMBER(5)                             -- 수강인원
);

COMMENT ON TABLE  courses                   IS '과정 정보';
COMMENT ON COLUMN courses.course_id         IS '과정코드';
COMMENT ON COLUMN courses.subject_id        IS '과목코드';
COMMENT ON COLUMN courses.course_name       IS '과정이름';
COMMENT ON COLUMN courses.course_desc       IS '과정 설명';
COMMENT ON COLUMN courses.start_date        IS '시작일';
COMMENT ON COLUMN courses.end_date          IS '종료일';
COMMENT ON COLUMN courses.manager_id        IS '담당자 코드';
COMMENT ON COLUMN courses.recruit_count     IS '모집정원';
COMMENT ON COLUMN courses.enroll_count      IS '수강인원';

-- 샘플 데이터 10개 삽입
INSERT ALL
    INTO courses (course_id, subject_id, course_name, course_desc, start_date, end_date, manager_id, recruit_count, enroll_count)
        VALUES (12956812, 231546, '고전읽기',   '고전을 읽고 토론하는 과정', TO_DATE('2018-02-05', 'YYYY-MM-DD'), TO_DATE('2018-06-05', 'YYYY-MM-DD'), 6547, 40, 38)
    INTO courses (course_id, subject_id, course_name, course_desc, start_date, end_date, manager_id, recruit_count, enroll_count)
        VALUES (12956813, 695745, '도시이해',   '도시의 구조와 문화를 이해하는 과정', TO_DATE('2018-02-06', 'YYYY-MM-DD'), TO_DATE('2018-06-06', 'YYYY-MM-DD'), 6586, 200, 50)
    INTO courses (course_id, subject_id, course_name, course_desc, start_date, end_date, manager_id, recruit_count, enroll_count)
        VALUES (12956814, 569846, '다문화사회', '다문화 사회의 특징을 배우는 과정', TO_DATE('2018-02-07', 'YYYY-MM-DD'), TO_DATE('2018-06-07', 'YYYY-MM-DD'), 6590, 50, 29)
    INTO courses (course_id, subject_id, course_name, course_desc, start_date, end_date, manager_id, recruit_count, enroll_count)
        VALUES (12956815, 320165, '연극치료',   '연극을 통한 심리치유 과정', TO_DATE('2018-02-08', 'YYYY-MM-DD'), TO_DATE('2018-06-08', 'YYYY-MM-DD'), 6798, 40, 30)
    INTO courses (course_id, subject_id, course_name, course_desc, start_date, end_date, manager_id, recruit_count, enroll_count)
        VALUES (12956816, 320166, '현대미술',   '현대미술의 흐름과 작가를 배우는 과정', TO_DATE('2018-03-01', 'YYYY-MM-DD'), TO_DATE('2018-07-01', 'YYYY-MM-DD'), 6801, 35, 32)
    INTO courses (course_id, subject_id, course_name, course_desc, start_date, end_date, manager_id, recruit_count, enroll_count)
        VALUES (12956817, 320167, '음악치료',   '음악을 활용한 치유 프로그램', TO_DATE('2018-03-02', 'YYYY-MM-DD'), TO_DATE('2018-07-02', 'YYYY-MM-DD'), 6802, 30, 27)
    INTO courses (course_id, subject_id, course_name, course_desc, start_date, end_date, manager_id, recruit_count, enroll_count)
        VALUES (12956818, 320168, '심리학개론', '심리학의 기초 이론을 배우는 과정', TO_DATE('2018-03-03', 'YYYY-MM-DD'), TO_DATE('2018-07-03', 'YYYY-MM-DD'), 6803, 60, 58)
    INTO courses (course_id, subject_id, course_name, course_desc, start_date, end_date, manager_id, recruit_count, enroll_count)
        VALUES (12956819, 320169, '컴퓨터입문', '컴퓨터의 기초를 배우는 과정', TO_DATE('2018-03-04', 'YYYY-MM-DD'), TO_DATE('2018-07-04', 'YYYY-MM-DD'), 6804, 100, 95)
    INTO courses (course_id, subject_id, course_name, course_desc, start_date, end_date, manager_id, recruit_count, enroll_count)
        VALUES (12956820, 320170, '영어회화',   '실생활 영어회화 중심 과정', TO_DATE('2018-03-05', 'YYYY-MM-DD'), TO_DATE('2018-07-05', 'YYYY-MM-DD'), 6805, 80, 77)
    INTO courses (course_id, subject_id, course_name, course_desc, start_date, end_date, manager_id, recruit_count, enroll_count)
        VALUES (12956821, 320171, '프로그래밍기초', '프로그래밍의 기초를 배우는 과정', TO_DATE('2018-03-06', 'YYYY-MM-DD'), TO_DATE('2018-07-06', 'YYYY-MM-DD'), 6806, 120, 110)
SELECT 1 FROM DUAL;

CREATE TABLE lecture_rooms (
    room_id         NUMBER(5)      PRIMARY KEY,         -- 강의실코드
    room_number     NUMBER(5)      NOT NULL,            -- 호수
    building_code   NUMBER(5)      NOT NULL             -- 강의관 코드
);

COMMENT ON TABLE  lecture_rooms                  IS '강의실 정보';
COMMENT ON COLUMN lecture_rooms.room_id          IS '강의실코드';
COMMENT ON COLUMN lecture_rooms.room_number      IS '호수';
COMMENT ON COLUMN lecture_rooms.building_code    IS '강의관 코드';

-- 샘플 데이터 삽입
INSERT ALL
    INTO lecture_rooms (room_id, room_number, building_code) VALUES (0, 504, 1)
    INTO lecture_rooms (room_id, room_number, building_code) VALUES (1, 202, 1)
    INTO lecture_rooms (room_id, room_number, building_code) VALUES (2, 303, 2)
    INTO lecture_rooms (room_id, room_number, building_code) VALUES (3, 101, 0)
SELECT 1 FROM DUAL;


CREATE TABLE subjects (
    subject_id        NUMBER(10)     PRIMARY KEY,         -- 과목ID
    subject_name      VARCHAR2(40)   NOT NULL,            -- 과목명
    subject_type_code NUMBER(5)      NOT NULL,            -- 교과구분코드
    credit            NUMBER(5)      NOT NULL,            -- 학점
    room_id           NUMBER(5)      NOT NULL,            -- 강의실 코드
    professor_id      NUMBER(5)      NOT NULL             -- 담당교수 교번
);

COMMENT ON TABLE  subjects                      IS '과목 정보';
COMMENT ON COLUMN subjects.subject_id           IS '과목ID';
COMMENT ON COLUMN subjects.subject_name         IS '과목명';
COMMENT ON COLUMN subjects.subject_type_code    IS '교과구분코드';
COMMENT ON COLUMN subjects.credit               IS '학점';
COMMENT ON COLUMN subjects.room_id              IS '강의실 코드';
COMMENT ON COLUMN subjects.professor_id         IS '담당교수 교번';

-- 샘플 데이터 삽입
INSERT ALL
    INTO subjects (subject_id, subject_name, subject_type_code, credit, room_id, professor_id) VALUES (231546, '고전읽기', 0, 2, 2, 82)
    INTO subjects (subject_id, subject_name, subject_type_code, credit, room_id, professor_id) VALUES (695745, '도시이해', 1, 2, 8, 135)
    INTO subjects (subject_id, subject_name, subject_type_code, credit, room_id, professor_id) VALUES (569846, '다문화사회', 2, 3, 2, 201)
    INTO subjects (subject_id, subject_name, subject_type_code, credit, room_id, professor_id) VALUES (320165, '연극치료', 3, 3, 4, 214)
SELECT 1 FROM DUAL;


CREATE TABLE student (
    no              NUMBER(10)      PRIMARY KEY,
    name            NVARCHAR2(20),
    grade           NUMBER(5),
    dept_code 		NUMBER(5),
    major_code      NUMBER(5),
    phone1          VARCHAR2(20),
    phone2          VARCHAR2(20),
    email           VARCHAR2(100),
    address         NVARCHAR2(150),
    photo           RAW(2000),
    status_code     NUMBER(5),
    CONSTRAINT fk_student_status
        FOREIGN KEY (status_code)
        REFERENCES student_status(status_code),
	CONSTRAINT fk_department
        FOREIGN KEY (dept_code)
        REFERENCES department(dept_code),
	CONSTRAINT fk_major
        FOREIGN KEY (major_code)
        REFERENCES major(major_code)
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
COMMENT ON COLUMN 	student.status_code 	IS '상태코드';

