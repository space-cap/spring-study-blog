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

CREATE TABLE student_status (
    status_code   NUMBER(5) 	PRIMARY KEY,  
    status_name   NVARCHAR2(20) 	NOT NULL   
);

