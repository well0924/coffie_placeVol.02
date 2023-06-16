package com.example.coffies_vol_02.board.repository;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.domain.QBoard;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.board.domain.dto.response.QBoardResponse;
import com.example.coffies_vol_02.member.domain.QMember;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Repository
public class CustomBoardRepositoryImpl implements CustomBoardRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public CustomBoardRepositoryImpl(EntityManager em){
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    //게시글 목록
    @Override
    public Page<BoardResponse> boardList(Pageable pageable) {

        List<BoardResponse>boardList = jpaQueryFactory
                .select(Projections.constructor(BoardResponse.class,QBoard.board))
                .from(QBoard.board)
                .join(QBoard.board.member,QMember.member).fetchJoin()
                .groupBy(QBoard.board.id)
                .orderBy(getAllOrderSpecifiers(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .distinct()
                .fetch();

        int totalCount = jpaQueryFactory
                .select(QBoard.board.count())
                .from(QBoard.board)
                .orderBy(getAllOrderSpecifiers(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .distinct()
                .fetch()
                .size();

        return new PageImpl<>(boardList,pageable,totalCount);
    }

    //게시물 검색
    @Override
    public Page<BoardResponse> findAllSearch(String searchVal, Pageable pageable) {

        List<BoardResponse> boardSearchResult = new ArrayList<>();

        //검색시 목록
        List<Board> result = jpaQueryFactory
                .select(QBoard.board)
                .from(QBoard.board)
                .join(QBoard.board.member,QMember.member).fetchJoin()
                .where(boardContentsEq(searchVal).or(boardAuthorEq(searchVal)).or(boardTitleEq(searchVal)))
                .orderBy(getAllOrderSpecifiers(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .distinct()
                .fetch();

        //검색시 게시물 갯수
        int resultCount = jpaQueryFactory
                .select(QBoard.board.count())
                .from(QBoard.board)
                .where(boardAuthorEq(searchVal).or(boardContentsEq(searchVal)).or(boardTitleEq(searchVal)))
                .orderBy(getAllOrderSpecifiers(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .size();


        for (Board board : result) {
            BoardResponse responseDto = new BoardResponse(board);
            boardSearchResult.add(responseDto);
        }
        return new PageImpl<>(boardSearchResult, pageable, resultCount);
    }
    
    //게시물 단일 조회
    @Override
    public BoardResponse boardDetail(int boardId) {
        BoardResponse result = jpaQueryFactory
                .select(new QBoardResponse(QBoard.board))
                .from(QBoard.board)
                .join(QBoard.board.member,QMember.member).fetchJoin()
                .where(QBoard.board.id.eq(boardId))
                .distinct()
                .fetchOne();

        return result;
    }

    BooleanBuilder boardContentsEq(String searchVal){
        return nullSafeBuilder(()-> QBoard.board.boardContents.contains(searchVal));
    }

    BooleanBuilder boardTitleEq(String searchVal){
        return nullSafeBuilder(()-> QBoard.board.boardTitle.contains(searchVal));
    }

    BooleanBuilder boardAuthorEq(String searchVal){
        return nullSafeBuilder(()-> QBoard.board.boardAuthor.contains(searchVal));
    }

    BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (Exception e) {
            return new BooleanBuilder();
        }
    }

    //게시글 정렬
    private List<OrderSpecifier> getAllOrderSpecifiers(Sort sort) {
        List<OrderSpecifier>orders =  new ArrayList<>();

        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            String prop = order.getProperty();

            System.out.println(order);
            System.out.println("direction:"+direction);
            System.out.println("prop:"+prop);

            PathBuilder<Board> orderByExpression =  new PathBuilder<>(Board.class,"board");
            System.out.println("orderByExpression:"+orderByExpression.get(prop));

            orders.add(new OrderSpecifier(direction,orderByExpression.get(prop)));
        });

        return orders;
    }
}
