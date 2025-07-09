package com.fastcampus.ch4;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.fastcampus.ch4.QBoard.board;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class BoardRepositoryTest2 {

    @Autowired
    private EntityManager em;

    @Autowired
    private BoardRepository boardRepository;


    private Board testBoard1;
    private Board testBoard2;
    private Board testBoard3;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        testBoard1 = new Board();
        testBoard1.setTitle("첫 번째 게시글");
        testBoard1.setWriter("홍길동");
        testBoard1.setContent("첫 번째 게시글의 내용입니다.");
        testBoard1.setViewCnt(10L);
        testBoard1.setInDate(new Date());
        testBoard1.setUpDate(new Date());

        testBoard2 = new Board();
        testBoard2.setTitle("두 번째 게시글");
        testBoard2.setWriter("김철수");
        testBoard2.setContent("두 번째 게시글의 내용입니다.");
        testBoard2.setViewCnt(25L);
        testBoard2.setInDate(new Date());
        testBoard2.setUpDate(new Date());

        testBoard3 = new Board();
        testBoard3.setTitle("Spring Boot 학습");
        testBoard3.setWriter("홍길동");
        testBoard3.setContent("Spring Boot를 학습하는 내용입니다.");
        testBoard3.setViewCnt(5L);
        testBoard3.setInDate(new Date());
        testBoard3.setUpDate(new Date());

        for (int i = 1; i <= 100; i++) {
            Board board = new Board();
            //board.setBno((long) i);
            board.setTitle("title" + i);
            board.setContent("content" + i);
            board.setWriter("writer" + (i % 5)); // writer0~4
            board.setViewCnt((long) (Math.random() * 100)); // 0~99
            board.setInDate(new Date());
            board.setUpDate(new Date());
            boardRepository.save(board);
        }
    }

    @Test
    @DisplayName("querydsl로 쿼리 작성 테스트3 - 동적 쿼리작성")
    public void querydslTest3() {
        String searchBy = "TC" ; // 제목(title)과 작성내용(content)에서 검색
        String keyword = "lee";
        keyword = "%" + keyword + "%";

        BooleanBuilder builder = new BooleanBuilder();

        // 동적으로 조건을 달리하게
        if(searchBy.equalsIgnoreCase("T"))
            builder.and(board.title.like(keyword));
        else if(searchBy.equalsIgnoreCase("C"))
            builder.and(board.content.like(keyword));
        else if(searchBy.equalsIgnoreCase("TC"))
            builder.and(board.title.like(keyword).or(board.content.like(keyword)));

        JPAQueryFactory qf = new JPAQueryFactory(em);
        JPAQuery query = qf.selectFrom(board)
                .where(builder)
                .orderBy(board.upDate.desc());

        List<Board> list = query.fetch();
        list.forEach(System.out::println);

    }


    @Test
    void testSaveBoard() {
        //QBoard board = QBoard.board;

        JPAQueryFactory qf = new JPAQueryFactory(em);

        JPAQuery<Board> query = qf.selectFrom(board)
                .where(board.title.eq(testBoard1.getTitle()));
        List<Board> boards = query.fetch();

        System.out.println("board size: " + boards.size());

        //assertThat(boards.size()).isEqualTo(1);
    }


}