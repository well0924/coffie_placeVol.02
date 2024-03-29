package com.example.coffies_vol_02.notice.repository;

import com.example.coffies_vol_02.notice.domain.NoticeBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface NoticeBoardRepository extends JpaRepository<NoticeBoard,Integer>,CustomNoticeBoardRepository,QuerydslPredicateExecutor {

}
