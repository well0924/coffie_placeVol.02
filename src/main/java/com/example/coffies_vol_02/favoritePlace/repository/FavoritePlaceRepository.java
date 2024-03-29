package com.example.coffies_vol_02.favoritePlace.repository;

import com.example.coffies_vol_02.favoritePlace.domain.FavoritePlace;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FavoritePlaceRepository extends JpaRepository<FavoritePlace,Integer>,CustomFavoritePlaceRepository {
    //위시리스트 확인
    boolean existsByPlaceIdAndMemberId(Integer placeId,Integer memberId);
}
