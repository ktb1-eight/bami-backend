package com.example.bami.city.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

@Slf4j
@RestController
@RequestMapping("/api/longstay")
@Tag(name = "장기 여행지 도시 추천", description = "장기 여행지로 추천된 도시의 정보를 조회 및 저장합니다.")
public class CityDescriptionController {

    @Operation(summary = "도시 설명 조회", description = "위키피디아에서 도시 이름을 기반으로 도시 설명을 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도시 설명 조회 성공", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/city-description/{cityName}")
    public String getCityDescription(
            @Parameter(description = "도시 이름", required = true) @PathVariable("cityName") String cityName) {
        try {
            String url = "https://ko.wikipedia.org/wiki/" + cityName;
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select(".mw-content-ltr.mw-parser-output p");

            return extractDescription(elements);

        } catch (HttpClientErrorException e) {
            return handleException(cityName, e, "클라이언트 오류로 도시 설명을 가져오지 못했습니다.");
        } catch (HttpServerErrorException e) {
            return handleException(cityName, e, "서버 오류로 도시 설명을 가져오지 못했습니다.");
        } catch (RestClientException e) {
            return handleException(cityName, e, "도시 설명을 가져오는 중 오류가 발생했습니다.");
        } catch (Exception e) {
            return handleException(cityName, e, "도시 설명을 가져오는 중 예기치 않은 오류가 발생했습니다.");
        }
    }

    private String extractDescription(Elements elements) {
        if (elements.size() > 1) {
            Element secondParagraph = elements.get(1);
            return secondParagraph.text();
        } else {
            return "설명을 찾을 수 없습니다.";
        }
    }

    private String handleException(String cityName, Exception e, String errorMessage) {
        log.error("{} for city {}: {}", errorMessage, cityName, e.getMessage());
        return errorMessage;
    }
}