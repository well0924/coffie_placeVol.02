package com.example.coffies_vol_02.Commnet.domain;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Config.BaseTime;
import com.example.coffies_vol_02.Like.domain.Like;
import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Place.domain.Place;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@ToString
@Table(name = "tbl_board_reply")
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String replyWriter;
    private String replyContents;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    @ToString.Exclude
    private Board board;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useridx")
    @ToString.Exclude
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    @ToString.Exclude
    private Place place;

    @ToString.Exclude
    @OneToMany(mappedBy = "comment",cascade = CascadeType.ALL)
    private Set<Like> likes = new HashSet<>();

    @Builder
    public Comment(String replyWriter,String replyContents,Board board,Member member){
        this.board = board;
        this.member = member;
        this.replyWriter = member.getUserId();
        this.replyContents = replyContents;

    }
}
