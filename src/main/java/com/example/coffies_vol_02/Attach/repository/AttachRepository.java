package com.example.coffies_vol_02.Attach.repository;

import com.example.coffies_vol_02.Attach.domain.Attach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface AttachRepository extends JpaRepository<Attach,Integer> {
    @Query("select a from Attach a where a.board.id = :id")
    List<Attach>findAttachBoard(@Param("id") Integer boardId)throws Exception;
    @Query("select a from Attach a where a.noticeBoard.id = :id")
    List<Attach>findAttachNoticeBoard(@Param("id") Integer noticeId)throws Exception;


}