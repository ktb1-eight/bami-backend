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
    public String getCityDescription(@PathVariable("cityName") String cityName) {
        try {
            String url = "https://ko.wikipedia.org/wiki/" + cityName;
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select(".mw-content-ltr.mw-parser-output p");

            // 두번째 <p> 태그 가져오기
            return extractDescription(elements);

        } catch (HttpClientErrorException e) {
            return handleException(cityName, e, "Client error occurred while fetching image.");
        } catch (HttpServerErrorException e) {
            return handleException(cityName, e, "Server error occurred while fetching image.");
        } catch (RestClientException e) {
            return handleException(cityName, e, "An error occurred while fetching image.");
        } catch (Exception e) {
            return handleException(cityName, e, "Unexpected error occurred while fetching image.");
        }
    }

    private String extractDescription(Elements elements) {
        if (elements.size() > 1) {
            Element secondParagraph = elements.get(1);
            return secondParagraph.text();
        } else {
            return "No description found.";
        }
    }

    private String handleException(String cityName, Exception e, String errorMessage) {
        log.error("{} for city {}: {}", errorMessage, cityName, e.getMessage());
        return errorMessage;
    }
}
