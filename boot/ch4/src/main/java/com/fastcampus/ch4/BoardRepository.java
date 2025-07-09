package com.fastcampus.ch4;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends CrudRepository<Board, Long> {

    @Query("select b from Board b")
    List<Board> findAll();

    @Query("select b from Board b where b.title=?1 and b.writer=?2")
    List<Board> findByTitleAndWriter(String title, String writer);

    @Query(value = "select * from board", nativeQuery = true)
    List<Board> findAllNative1();

    @Query(value = "select title, writer from board", nativeQuery = true)
    List<Object[]> findAllNative2();

    // 제목으로 검색
    List<Board> findByTitleContaining(String title);

    // 작성자로 검색
    List<Board> findByWriter(String writer);

    // 조회수 기준으로 정렬
    List<Board> findAllByOrderByViewCntDesc();

    // 제목과 내용에서 키워드 검색
    @Query("SELECT b FROM Board b WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword%")
    List<Board> findByTitleOrContentContaining(@Param("keyword") String keyword);

    //int countByTitle(String title);
    //List<Board> findByTitle(String title);

    //List<Board> findByWriter(String writer);
    //List<Board> findByTitleAndWriter(String title, String writer);
    //List<Board> findByWriterAndTitle(String writer, String title);

    //int countByWriterAndTitle(String writer, String title);
    //int countByTitleAndWriter(String title, String writer);

    @Transactional
    int deleteByBno(long bno);

}
