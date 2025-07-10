package com.fastcampus.ch4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    BoardService boardService;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/modify")
    public String modify(Long bno, Model model){
        Board board = boardService.read(bno);
        model.addAttribute("board", board);
        return "/board/write";
    }

    @PostMapping("/modify")
    public String modify(Board board){
        boardService.modify(board);

        return "redirect:/board/list";
    }
    
    @GetMapping("/write")
    public String showWriteForm(Model model){
        Board board = new Board();
        User user = new User();
        user.setId("aaa");
        board.setUser(user);


        System.out.println("board:" + board);
        model.addAttribute("board", board);
        return "board/write";
    }

    @PostMapping("/write")
    public String write(Board board){
        // board.setBno(11L); // 자동 증가 기능 사용하는게 바람직.
        // User user = new User();
        // user.setId("aaa");
        // board.setUser(user);

        // 2. orElse() 사용
        // User user = userRepository.findById(userId)
        //        .orElse(null);  // 또는 기본값


        // 3. isPresent() 체크
        /*Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // 로직 처리
        } else {
            // 사용자 없음 처리
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다");
        }*/

        // 4. ifPresent() 사용
        /*userRepository.findById(userId)
                .ifPresent(user -> {
                    board.setUser(user);
                    // 추가 로직
                });*/

        // 5. Custom Exception 사용
       /* User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));*/





        //User user = userRepository.findById(userId)
        //        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));


        //board.setUser(user);
        board.setViewCnt(0L);
        board.setInDate(new Date());
        board.setUpDate(new Date());
        boardService.write(board);

        return "redirect:/board/list"; // 글을 삭제한 다음에는 게시물 목록으로 이동
    }

    @PostMapping("/remove")
    public String remove(Long bno){
        boardService.remove(bno);
        return "redirect:/board/list"; // 게시물 삭제후에 게시물 목록으로 이동
    }

    @GetMapping("/read")
    public String read(Long bno, Model model) {
        Board board = boardService.read(bno);
        model.addAttribute("board", board);
        return "/board/read";  // read.html을 뷰로 사용.
    }

    @GetMapping("/list")
    public String getList(Model model) {
        List<Board> list = boardService.getList();
        model.addAttribute("list", list);

        return "board/list";
    }


}
