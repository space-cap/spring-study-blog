package com.fastcampus.ch4;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Board {
    // no usages
    @Id
    @GeneratedValue // 자동 번호 증가
    private Long bno; // 게시글 번호

    // no usages
    private String title;

    // no usages
    private String writer;

    // no usages
    private String content;

    // no usages
    private Long viewCnt;

    // no usages
    @Temporal(value= TemporalType.TIMESTAMP)
    private Date inDate;

    // no usages
    @Temporal(value= TemporalType.TIMESTAMP)
    private Date upDate;

    public Long getBno() {
        return bno;
    }

    public void setBno(Long bno) {
        this.bno = bno;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getViewCnt() {
        return viewCnt;
    }

    public void setViewCnt(Long viewCnt) {
        this.viewCnt = viewCnt;
    }

    public Date getInDate() {
        return inDate;
    }

    public void setInDate(Date inDate) {
        this.inDate = inDate;
    }

    public Date getUpDate() {
        return upDate;
    }

    public void setUpDate(Date upDate) {
        this.upDate = upDate;
    }

    @Override
    public String toString() {
        return "Board{" +
                "bno=" + bno +
                ", title='" + title + '\'' +
                ", writer='" + writer + '\'' +
                ", content='" + content + '\'' +
                ", viewCnt=" + viewCnt +
                ", inDate=" + inDate +
                ", upDate=" + upDate +
                '}';
    }
}

