package com.example.coffies_vol_02.place.controller.view;

import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.place.domain.PlaceImage;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceImageResponseDto;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import com.example.coffies_vol_02.place.service.PlaceImageService;
import com.example.coffies_vol_02.place.service.PlaceService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Controller
@AllArgsConstructor
@RequestMapping("/page/place")
public class PlaceViewController {
    private final PlaceService placeService;
    private final PlaceImageService placeImageService;

    @GetMapping("/list")
    public ModelAndView placeList(Pageable pageable,
                                  @RequestParam(value = "keyword",required = false) String keyword,
                                  @RequestParam(value = "searchType",required = false) SearchType searchType,
                                  @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ModelAndView mv = new ModelAndView();

        Slice<PlaceResponseDto> list = null;
        Page<PlaceResponseDto>searchList = null;
        List<String> keywords = new ArrayList<>();

        try {
            if (customUserDetails != null) {
                //가게 검색어 저장 목록
                keywords = placeService.placeSearchList(customUserDetails.getMember());
                //가게 목록
                list = placeService.placeSlideList(pageable, keyword, customUserDetails.getMember());
                log.info("placeList:::"+list);
                if(keyword==null|| keyword.equals("")){
                    searchList = placeService.placeListAll(searchType,keyword,pageable,customUserDetails.getMember());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mv.addObject("placelist", list);
        mv.addObject("keyword", keyword);
        mv.addObject("keywords", keywords);
        mv.addObject("searchList",searchList);

        mv.setViewName("/place/placelist");

        return mv;
    }

    @GetMapping("/detail/{place_id}")
    public ModelAndView placeDetail(@PathVariable("place_id") Integer placeId) {
        ModelAndView mv = new ModelAndView();

        PlaceResponseDto detail = new PlaceResponseDto();
        List<PlaceImageResponseDto> imageResponseDtoList = new ArrayList<>();

        try {
            detail = placeService.placeDetail(placeId);
            imageResponseDtoList = placeImageService.placeImageResponseDtoList(placeId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mv.addObject("detail", detail);
        mv.addObject("imagelist", imageResponseDtoList);

        mv.setViewName("/place/placedetail");

        return mv;
    }

    @GetMapping("/placeregister")
    public ModelAndView placeRegister() {
        ModelAndView mv = new ModelAndView();

        String uuid = UUID.randomUUID().toString();
        String key = "place_" + uuid.substring(0, uuid.indexOf("-"));

        mv.addObject("fileGroupId", key);
        mv.setViewName("/place/placeregister");

        return mv;
    }

    @GetMapping("/placemodify/{place-id}")
    public ModelAndView placeModify(@PathVariable("place-id") Integer placeId) {
        ModelAndView mv = new ModelAndView();

        PlaceResponseDto detail = new PlaceResponseDto();
        List<PlaceImageResponseDto>placeImages = new ArrayList<>();

        try {
            detail = placeService.placeDetail(placeId);
            placeImages = placeImageService.placeImageResponseDtoList(placeId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mv.addObject("detail", detail);
        mv.addObject("placeImages",placeImages);

        mv.setViewName("/place/placemodify");

        return mv;
    }
}
