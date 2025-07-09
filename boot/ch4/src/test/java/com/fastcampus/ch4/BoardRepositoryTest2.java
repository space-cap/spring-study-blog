package com.fastcampus.ch4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class BoardRepositoryTest2 {

    @Autowired
    private TestEntityManager entityManager;

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
    }


    @Test
    void testSaveBoard() {
        // Given
        Board board = new Board();
        board.setTitle("테스트 게시글");
        board.setWriter("테스터");
        board.setContent("테스트 내용");
        board.setViewCnt(0L);
        board.setInDate(new Date());
        board.setUpDate(new Date());

        // When
        Board savedBoard = boardRepository.save(board);

        // Then
        assertThat(savedBoard).isNotNull();
        assertThat(savedBoard.getBno()).isNotNull();
        assertThat(savedBoard.getTitle()).isEqualTo("테스트 게시글");
        assertThat(savedBoard.getWriter()).isEqualTo("테스터");
        assertThat(savedBoard.getContent()).isEqualTo("테스트 내용");
        assertThat(savedBoard.getViewCnt()).isEqualTo(0L);
    }

    @Test
    void testFindById() {
        // Given
        Board savedBoard = entityManager.persistAndFlush(testBoard1);

        // When
        Optional<Board> foundBoard = boardRepository.findById(savedBoard.getBno());

        // Then
        assertThat(foundBoard).isPresent();
        assertThat(foundBoard.get().getTitle()).isEqualTo("첫 번째 게시글");
        assertThat(foundBoard.get().getWriter()).isEqualTo("홍길동");
    }

    @Test
    public void insertTest()
    {
        Board board = new Board();
        //board.setBno(1L);
        board.setTitle("lee");
        board.setContent("content");
        board.setWriter("writer");
        board.setViewCnt(0L);
        board.setInDate(new Date());
        board.setUpDate(new Date());
        boardRepository.save(board);

        board.setBno(null);
        boardRepository.save(board);
        board.setBno(null);
        boardRepository.save(board);
    }

    @Test
    public void findAllTest() {
        List<Board> boards = boardRepository.findAll();
        System.out.println("board size: " + boards.size());
    }

    @Test
    public void findAllNative1Test() {
        List<Object[]> list = boardRepository.findAllNative2();
        System.out.println("list size: " + list.size());

        list.stream().map(arr-> Arrays.toString(arr)).forEach(System.out::println);
    }

}