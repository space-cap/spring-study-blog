package com.fastcampus.ch4;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    //@Query("SELECT b FROM Board b LEFT JOIN FETCH b.user")
    //List<Board> findAllWithUser();

}
