package com.fastcampus.ch4;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;





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