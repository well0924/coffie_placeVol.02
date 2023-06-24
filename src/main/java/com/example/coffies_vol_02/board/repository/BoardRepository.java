package com.example.coffies_vol_02.board.repository;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.domain.dto.response.BoardNextPreviousInterface;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BoardRepository extends JpaRepository<Board,Integer>,CustomBoardRepository,QuerydslPredicateExecutor {

    /**
     * 비밀 번호 확인
     **/
    @Query("select b from Board b where b.id=:id and b.passWd= :passWd")
    BoardResponse findByPassWdAndId(@Param("passWd") String password, @Param("id") Integer id);

    /**
     * 내가 작성한 글
     **/
    Page<Board> findByMember(Member member, Pageable pageable);

    /**
     * 게시글 조회수 증가(동시성을 고려해서 적용해보기)
     **/
    @Transactional
    @Modifying
    @Query("update Board b set b.readCount = :readCount+1 where b.id = :id")
    void ReadCountUpToDB(@Param("id")Integer id, @Param("readCount")Integer readCount);

    /**
     * 게시글 다음글
     **/
    @Query(nativeQuery = true,value = "select tb.id,tb.board_title as BoardTitle from tbl_board tb " +
            "where tb.id > :id order by tb.id limit 1")
    BoardNextPreviousInterface findNextBoard(@Param("id")Integer boardId);

    /**
     * 게시글 이전글
     **/
    @Query(nativeQuery = true,value = "select tb.id,tb.board_title as BoardTitle from tbl_board tb " +
            "where tb.id < :id order by tb.id desc limit 1")
    BoardNextPreviousInterface findPreviousBoard(@Param("id")Integer boardId);
}
