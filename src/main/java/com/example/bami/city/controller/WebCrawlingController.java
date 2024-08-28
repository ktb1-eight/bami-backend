package com.example.bami.city.controller;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

@Slf4j
@RestController
public class WebCrawlingController {

    @GetMapping("/api/city-description/{cityName}")
    public String getCityDescription(@PathVariable("cityName") String cityName){
        try {
            String url = "https://ko.wikipedia.org/wiki/" + cityName;
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select(".mw-content-ltr.mw-parser-output p");

            //두번째 <p> 태그 가져오기
            if (elements.size() > 1) {
                Element secondParagraph = elements.get(1);
                return secondParagraph.text();
            } else {
                return "No description found.";
            }
        } catch (HttpClientErrorException e) {
            // 클라이언트 오류 처리 (4xx)
            log.error("Client error occurred while fetching image for city {}: {}", cityName, e.getMessage());
            return "Client error occurred while fetching image.";
        } catch (HttpServerErrorException e) {
            // 서버 오류 처리 (5xx)
            log.error("Server error occurred while fetching image for city {}: {}", cityName, e.getMessage());
            return "Server error occurred while fetching image.";
        } catch (RestClientException e) {
            // 기타 RestTemplate 관련 오류 처리
            log.error("Error occurred while fetching image for city {}: {}", cityName, e.getMessage());
            return "An error occurred while fetching image.";
        } catch (Exception e) {
            // 그 외의 모든 예외 처리
            log.error("Unexpected error occurred while fetching image for city {}: {}", cityName, e.getMessage());
            return "Unexpected error occurred while fetching image.";
        }
    }
}
