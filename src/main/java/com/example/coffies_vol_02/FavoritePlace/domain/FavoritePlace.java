package com.example.coffies_vol_02.FavoritePlace.domain;

import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Place.domain.Place;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "tbl_favorite_place")
@ToString(exclude = {"place","member"})
@NoArgsConstructor
public class FavoritePlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private Place place;
    @ManyToOne
    private Member member;
    private String fileGroupId;

    @Builder
    public FavoritePlace(Integer id,Place place, Member member, String fileGroupId) {
        this.id = id;
        this.place = place;
        this.member = member;
        this.fileGroupId = place.getFileGroupId();
    }
}
