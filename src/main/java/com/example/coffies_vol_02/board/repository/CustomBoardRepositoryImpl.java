package com.example.coffies_vol_02.board.repository;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.domain.QBoard;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.board.domain.dto.response.QBoardResponse;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.like.domain.QLike;
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
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CustomBoardRepositoryImpl implements CustomBoardRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public CustomBoardRepositoryImpl(EntityManager em){
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    /**
     * 게시글 목록
     * @author 양경빈
     * @param pageable 게시물 목록에서 페이징에 필요한 객체
     * @return Page<BoardResponse> 게시물 목록
     **/
    @Override
    public Page<BoardResponse> boardList(Pageable pageable) {

        List<BoardResponse>boardList = jpaQueryFactory
                .select(Projections.constructor(BoardResponse.class,QBoard.board))
                .from(QBoard.board)
                .join(QBoard.board.member,QMember.member).fetchJoin()
                .groupBy(QBoard.board.id)
                .orderBy(getAllOrderSpecifiers(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
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

    /**
     * 게시글 검색
     * @author 양경빈
     * @param searchVal 자유게시물 목록에서 검색에 필요한 검색어
     * @param pageable 게시물 목록에서 페이징에 필요한 객체
     * @return Page<BoardResponse> 게시물 목록
     **/
    @Override
    public Page<BoardResponse> findAllSearch(SearchType searchType, String searchVal, Pageable pageable) {

        List<BoardResponse> boardSearchResult = new ArrayList<>();
        //검색시 목록
        List<Board> result = boardSearchList(searchType,searchVal,pageable);
        //검색시 게시물 갯수
        int resultCount = searchResultCount(searchType,searchVal,pageable);

        for (Board board : result) {
            BoardResponse responseDto = new BoardResponse(board);
            boardSearchResult.add(responseDto);
        }

        return new PageImpl<>(boardSearchResult, pageable, resultCount);
    }

    /**
     * 게시물 단일 조회
     * @author 양경빈
     * @param boardId 게시물 번호
     * @return BoardResponse
     **/
    @Override
    public BoardResponse boardDetail(int boardId) {

        return jpaQueryFactory
                .select(new QBoardResponse(QBoard.board))
                .from(QBoard.board)
                .join(QBoard.board.member,QMember.member).fetchJoin()
                .where(QBoard.board.id.eq(boardId))
                .distinct()
                .fetchOne();
    }

    /**
     * 좋아요를 한 게시물목록 조회
     * @author 양경빈
     * @param useridx 회원 번호
     * @return List<BoardResponse>result 게시물 목록
     **/
    @Override
    public Page<BoardResponse> likedBoardDetailList(int useridx,Pageable pageable) {
        List<BoardResponse>result = new ArrayList<>();

        List<Board>like = likeBoard(useridx);

        int count = likeBoardCount(useridx);

        for(Board board : like){
            BoardResponse response = new BoardResponse(board);
            result.add(response);
        }
        return new PageImpl<>(result,pageable,count);
    }

    /**
     * 게시글 검색 목록
     * @param searchVal 검색어
     * @param pageable 페이징 객체
     * @return List<Board>
     **/
    private List<Board> boardSearchList(SearchType searchType,String searchVal,Pageable pageable){
        return jpaQueryFactory
                .select(QBoard.board)
                .from(QBoard.board)
                .join(QBoard.board.member,QMember.member).fetchJoin()
                .where(switch (searchType){
                    case t -> boardTitleEq(searchVal);
                    case c -> boardContentsEq(searchVal);
                    case w -> boardAuthorEq(searchVal);
                    case i, e, n, a, p -> null;
                    case all -> boardContentsEq(searchVal).and(boardContentsEq(searchVal)).and(boardAuthorEq(searchVal));
                })
                .orderBy(getAllOrderSpecifiers(pageable.getSort())
                        .toArray(OrderSpecifier[]::new))
                .distinct()
                .fetch();
    }

    /**
     * 게시글 검색 총 갯수
     * @param searchVal 검색어
     * @param pageable 페이징 객체
     * @return BoardCount(int) 게시글 갯수
     **/
    private int searchResultCount(SearchType searchType,String searchVal,Pageable pageable){
        return jpaQueryFactory
                .select(QBoard.board.count())
                .from(QBoard.board)
                .where(switch (searchType){
                    //switch 문 (java 14)
                    case t -> boardTitleEq(searchVal);
                    case c -> boardContentsEq(searchVal);
                    case w -> boardAuthorEq(searchVal);
                    case i, a, p, n, e -> null;
                    case all -> boardContentsEq(searchVal).and(boardContentsEq(searchVal)).and(boardAuthorEq(searchVal));
                })
                .orderBy(getAllOrderSpecifiers(pageable.getSort())
                        .toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .size();
    }
    
    //좋아요를 한 게시글 목록
    private List<Board>likeBoard(int useridx){
        return jpaQueryFactory.select(QBoard.board)
                .from(QBoard.board)
                .innerJoin(QLike.like)
                .on(QBoard.board.id.eq(QLike.like.board.id))
                .where(QLike.like.member.id.eq(useridx))
                .fetch();
    }
    
    //좋아요를 한 게시글 수
    private int likeBoardCount(int useridx){
        return jpaQueryFactory.select(QBoard.board)
                .from(QBoard.board)
                .innerJoin(QLike.like)
                .on(QBoard.board.id.eq(QLike.like.board.id))
                .where(QLike.like.member.id.eq(useridx))
                .fetch()
                .size();
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

    /**
     * 동적정렬
     * @param sort 페이징객체에서 정렬을 하는 객체
     * @return List<OrderSpecifier>orders 정렬된 목록 값 기본값은 오름차순
     **/
    private List<OrderSpecifier> getAllOrderSpecifiers(Sort sort) {
        List<OrderSpecifier>orders =  new ArrayList<>();

        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            String prop = order.getProperty();

            PathBuilder<Board> orderByExpression =  new PathBuilder<>(Board.class,"board");

            orders.add(new OrderSpecifier(direction,orderByExpression.get(prop)));
        });

        return orders;
    }
}
