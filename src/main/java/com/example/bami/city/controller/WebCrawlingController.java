package com.example.bami.city.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred.";
        }
    }
}
