package com.fastcampus.ch2;

class MyDate2 {
    private int year;
    private int month;
    private int day;
    private String description;
    
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public int getDay() { return day; }
    public void setDay(int day) { this.day = day; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return year + "-" + month + "-" + day + " (" + description + ")";
    }
}
