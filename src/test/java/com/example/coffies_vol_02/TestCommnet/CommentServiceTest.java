package com.example.coffies_vol_02.TestCommnet;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Board.repository.BoardRepository;
import com.example.coffies_vol_02.Commnet.domain.Comment;
import com.example.coffies_vol_02.Commnet.domain.dto.CommentDto;
import com.example.coffies_vol_02.Commnet.repository.CommentRepository;
import com.example.coffies_vol_02.Commnet.service.CommentService;
import com.example.coffies_vol_02.Config.Exception.ERRORCODE;
import com.example.coffies_vol_02.Config.Exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Member.domain.Role;
import com.example.coffies_vol_02.Member.domain.dto.MemberDto;
import com.example.coffies_vol_02.Member.repository.MemberRepository;
import com.example.coffies_vol_02.Place.domain.Place;
import com.example.coffies_vol_02.Place.repository.PlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private MemberRepository memberRepository;

    Member member;

    Board board;

    Place place;

    Comment comment;

    MemberDto.MemberResponseDto responseDto;

    BoardDto.BoardResponseDto boardResponseDto;

    CommentDto.CommentResponseDto commentResponseDto;

    @BeforeEach
    public void init(){
        member = memberDto();
        responseDto = responseDto();
        comment = comment();
        commentResponseDto = commentResponseDto();
        boardResponseDto = boardResponseDto();
        board = board();
        place = place();
    }

    @DisplayName("댓글 목록-성공")
    @Test
    public void CommentListTest() throws Exception {
        List<CommentDto.CommentResponseDto>result = new ArrayList<>();
        List<Comment>list = new ArrayList<>();
        list.add(comment);
        result.add(commentResponseDto);

        given(commentRepository.findByBoardId(board.getId())).willReturn(list);
        given(commentService.replyList(board.getId())).willReturn(anyList());

        when(commentRepository.findByBoardId(board.getId())).thenReturn(list);
        when(commentService.replyList(board.getId())).thenReturn(anyList());
        result = commentService.replyList(board.getId());

        verify(commentRepository).findByBoardId(board.getId());
    }

    @Test
    @DisplayName("댓글 작성-성공")
    public void CommentWriteTest(){
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        CommentDto.CommentRequestDto commentRequestDto = new CommentDto.CommentRequestDto();
        commentRequestDto.setReplyContents(comment().getReplyContents());
        commentRequestDto.setReplyWriter(member.getUserId());

        given(commentRepository.save(comment)).willReturn(comment);
        given(commentService.commentCreate(board.getId(),member,commentRequestDto)).willReturn(any());

        when(commentService.commentCreate(board.getId(),member,commentRequestDto)).thenReturn(any());

        then(commentService.commentCreate(board.getId(),member,commentRequestDto));
    }

    @Test
    @DisplayName("게시판 댓글작성 실패-로그인이 안된 경우")
    public void CommentWriteFail(){
        given(memberRepository.findById(anyInt())).willReturn(Optional.empty());
        given(boardRepository.findById(anyInt())).willReturn(Optional.of(board));

        member= null;

        CommentDto.CommentRequestDto commentRequestDto = new CommentDto.CommentRequestDto();
        commentRequestDto.setReplyContents(comment.getReplyContents());
        commentRequestDto.setReplyWriter(null);
        commentRequestDto.setReplyPoint(comment.getReplyPoint());

        assertThatThrownBy(()->commentService.commentCreate(anyInt(),null,commentRequestDto))
                .isInstanceOf(CustomExceptionHandler.class);
    }

    @Test
    @DisplayName("댓글 삭제-성공")
    public void CommentDeleteTest(){
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        doNothing().when(commentRepository).deleteById(comment.getId());
        commentService.commentDelete(comment.getId(),member);

        verify(commentRepository).deleteById(comment.getId());
    }

    @Test
    @DisplayName("댓글 삭제실패-로그인이 안 된 경우")
    public void CommentDeleteFail1(){
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));

        assertThatThrownBy(()->commentService.commentDelete(board.getId(),null))
                .isInstanceOf(CustomExceptionHandler.class);
    }

    @Test
    @DisplayName("댓글 삭제실패-작성자가 다른 경우")
    public void CommentDeleteFail2(){
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));

        String userid = "well";
        member.setUserId(userid);

        assertThatThrownBy(()->commentService.commentDelete(anyInt(),member))
                .isInstanceOf(CustomExceptionHandler.class);
    }

    @Test
    @DisplayName("가게 댓글목록")
    public void PlaceCommentListTest() throws Exception {
       List<Comment>commentList = new ArrayList<>();
       commentList.add(comment);
       List<CommentDto.CommentResponseDto>list = new ArrayList<>();
       list.add(commentResponseDto);

       given(commentRepository.findByPlaceId(place.getId())).willReturn(commentList);
       given(commentService.placeCommentList(place.getId())).willReturn(anyList());

       when(commentService.placeCommentList(place.getId())).thenReturn(anyList());

       commentService.placeCommentList(place.getId());

       verify(commentRepository).findByPlaceId(any());
    }

    @Test
    @DisplayName("가게 댓글 작성")
    public void PlaceCommentCreateTest(){
        given(placeRepository.findById(anyInt())).willReturn(Optional.of(place));

        CommentDto.CommentRequestDto commentRequestDto = new CommentDto.CommentRequestDto();
        commentRequestDto.setReplyContents(comment.getReplyContents());
        commentRequestDto.setReplyWriter(member.getUserId());
        commentRequestDto.setReplyPoint(comment.getReplyPoint());

        given(commentService.placeCommentCreate(place.getId(),commentRequestDto,member)).willReturn(anyInt());
        when(commentService.placeCommentCreate(place.getId(),commentRequestDto,member)).thenReturn(anyInt());
        then(commentService.placeCommentCreate(place.getId(),commentRequestDto,member));
    }

    @Test
    @DisplayName("가게 댓글 작성-실패(로그인을 안한 경우)")
    public void PlaceCommentCreateTestFail1(){
        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));
        given(memberRepository.findById(member.getId())).willReturn(Optional.empty());
        member = null;
        CommentDto.CommentRequestDto commentRequestDto = new CommentDto.CommentRequestDto();
        commentRequestDto.setReplyContents(comment.getReplyContents());
        commentRequestDto.setReplyWriter(null);
        commentRequestDto.setReplyPoint(comment.getReplyPoint());

        CustomExceptionHandler customExceptionHandler = assertThrows(CustomExceptionHandler.class,()-> {
            commentService.placeCommentCreate(place.getId(),commentRequestDto,null);
        });
        System.out.println(customExceptionHandler);
        assertThat(customExceptionHandler.getErrorCode()).isEqualTo(ERRORCODE.ONLY_USER);
    }
    
    @Test
    @DisplayName("가게 댓글 삭제")
    public void PlaceCommentDeleteTest(){
        //given
        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));
        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        //when
        doNothing().when(commentRepository).deleteById(comment.getId());
        commentService.placeCommentDelete(comment.getId(),member);
        //then
        verify(commentRepository).deleteById(comment.getId());
    }

    @Test
    @DisplayName("가게 댓글 삭제-실패(로그인을 하지 않은경우)")
    public void PlaceCommentDeleteTestFail1(){
        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));
        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));

        CustomExceptionHandler customExceptionHandler = assertThrows(CustomExceptionHandler.class,()-> {
            commentService.placeCommentDelete(place.getId(),null);
        });
        assertThat(customExceptionHandler.getErrorCode()).isEqualTo(ERRORCODE.ONLY_USER);
    }

    @Test
    @DisplayName("가게 댓글 삭제-실패(작성자가 다른경우)")
    public void PlaceCommentDeleteTestFail2(){
        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));
        given(commentRepository.findById(place.getId())).willReturn(Optional.of(comment));

        String userId = "we";
        member.setUserId(userId);

        CustomExceptionHandler customExceptionHandler = assertThrows(CustomExceptionHandler.class,()-> {
            commentService.placeCommentDelete(place.getId(),member);
        });
        System.out.println(customExceptionHandler);
        assertThat(customExceptionHandler.getErrorCode()).isEqualTo(ERRORCODE.NOT_AUTH);
    }

    @Test
    @DisplayName("댓글 평점계산")
    public void replyPointTest() throws Exception {
        given(commentRepository.getStarAvgByPlaceId(place.getId())).willReturn(place.getReviewRate());

        when(commentService.getStarAvgByPlaceId(place.getId())).thenReturn(place.getReviewRate());
        Double starAvg = commentService.getStarAvgByPlaceId(place.getId());

        verify(commentRepository,atLeastOnce()).getStarAvgByPlaceId(place.getId());
    }

    @Test
    @DisplayName("가게 평점 출력")
    public void cafeReviewRateTest() throws Exception {
        given(commentRepository.getStarAvgByPlaceId(place.getId())).willReturn(place.getReviewRate());

        doNothing().when(commentRepository).cafeReviewRate(place.getReviewRate(),place.getId());
        commentService.cafeReviewRate(place.getReviewRate(),place.getId());

        verify(commentRepository).cafeReviewRate(anyDouble(),anyInt());
    }

    @Test
    @DisplayName("가게 댓글 평점 출력")
    public void updateStarTest() throws Exception {
        given(commentRepository.getStarAvgByPlaceId(place.getId())).willReturn(place.getReviewRate());

        doNothing().when(commentRepository).cafeReviewRate(place.getReviewRate(),place.getId());
        commentService.cafeReviewRate(place.getReviewRate(),place.getId());
        commentService.updateStar(place.getId());

    }

    private Comment comment(){
        return Comment
                .builder()
                .replyContents("reply test")
                .replyWriter(member.getUserId())
                .replyPoint(3)
                .board(board)
                .member(member)
                .place(place)
                .build();
    }
    private Board board(){
        return Board
                .builder()
                .id(1)
                .boardAuthor(member.getUserId())
                .boardTitle("test")
                .boardContents("test!")
                .passWd("132v")
                .readCount(0)
                .fileGroupId("free_weft33")
                .member(member)
                .build();
    }
    private Member memberDto(){
        return Member
                .builder()
                .id(1)
                .userId("well4149")
                .password("qwer4149!!")
                .memberName("userName")
                .userEmail("well414965@gmail.com")
                .userPhone("010-9999-9999")
                .userAge("20")
                .userGender("남자")
                .userAddr1("xxxxxx시 xxxx")
                .userAddr2("ㄴㅇㄹㅇㄹㅇ")
                .role(Role.ROLE_ADMIN)
                .build();
    }
    private Place place(){
        return Place
                .builder()
                .id(1)
                .placeLng(123.3443)
                .placeLat(23.34322)
                .placeAddr1("xxxx시 xx구")
                .placeAddr2("ㅁㄴㅇㄹ")
                .placeStart("09:00")
                .placeClose("18:00")
                .placeAuthor("admin")
                .placePhone("010-3444-3654")
                .reviewRate(3.0)
                .fileGroupId("place_fre353")
                .placeName("test place1")
                .build();
    }

    private MemberDto.MemberResponseDto responseDto(){
        return MemberDto.MemberResponseDto
                .builder()
                .id(1)
                .userId("well4149")
                .password(memberDto().getPassword())
                .memberName("userName")
                .userEmail("well414965@gmail.com")
                .userPhone("010-9999-9999")
                .userGender("남자")
                .userAddr1("xxxxxx시 xxxx")
                .userAddr2("ㄴㅇㄹㅇㄹㅇ")
                .role(Role.ROLE_ADMIN)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }
    private BoardDto.BoardResponseDto boardResponseDto(){
        return BoardDto.BoardResponseDto.builder()
                .id(board().getId())
                .boardTitle(board().getBoardTitle())
                .boardAuthor(board().getBoardAuthor())
                .boardContents(board().getBoardContents())
                .fileGroupId(board().getFileGroupId())
                .readCount(board().getReadCount())
                .passWd(board().getPassWd())
                .updatedTime(LocalDateTime.now())
                .createdTime(LocalDateTime.now())
                .build();
    }

    private CommentDto.CommentResponseDto commentResponseDto(){
        return CommentDto.CommentResponseDto
                .builder()
                .comment(comment())
                .build();
    }
}
