package com.example.bami.weather.service;

import com.example.bami.weather.dto.WeatherDTO;
import io.jsonwebtoken.io.IOException;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


@Service
@Slf4j
public class WeatherService {
    private final WebClient webClient;
    private final String key;
    private static final String CATEGORY = "category";

    @Autowired
    public WeatherService(@Value("${weather.key}") String key) {
        HttpClient tcpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        ExchangeStrategies es = ExchangeStrategies.builder()
                .codecs(config -> config.defaultCodecs().maxInMemorySize(-1))
                .build();
        this.webClient = WebClient.builder()
                .exchangeStrategies(es)
                .clientConnector(new ReactorClientHttpConnector(tcpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.key = key;
    }

    public double getTemparature(WeatherDTO q){
        double fcstValue = 0;
        JSONArray parseItem = getForecast(q);

        for (Object o : parseItem) {
            JSONObject weather = (JSONObject) o;
            if (!(weather.get(CATEGORY)).equals("T1H")) {
                continue;
            }

            fcstValue = Double.parseDouble(weather.get("fcstValue").toString());

            log.info("\tcategory : " + weather.get(CATEGORY) +
                    ", fcst_Value : " + fcstValue +
                    ", fcstDate : " + weather.get("fcstDate") +
                    ", fcstTime : " + weather.get("fcstTime"));
            break;
        }


        return fcstValue;
    }

    public double[] getHighLowTemperature(WeatherDTO q){
        double lowVal = 100;
        double highVal = 0;

        JSONArray parseItem = getForecast(q);
        JSONObject weather; // parse_item은 배열형태이기 때문에 하나씩 데이터를 하나씩 가져올때 사용

        for (Object o : parseItem) {
            weather = (JSONObject) o;
            if (!(weather.get(CATEGORY)).equals("T1H")) continue;

            double fcstValue = Double.parseDouble(weather.get("fcstValue").toString());

            lowVal = Math.min(fcstValue, lowVal);
            highVal = Math.max(fcstValue, highVal);
            log.info(weather.get("fcstTime") + " " + weather.get("fcstDate") + " " + fcstValue);
        }


        return new double[] {lowVal, highVal};
    }
    private JSONArray getForecast(WeatherDTO p) {
        JSONArray parseItem = null;

        String uriString =
                String.format("https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst?serviceKey=%s&pageNo=%s&numOfRows=%s&dataType=json&base_date=%s&base_time=%s&nx=%d&ny=%d",
                        this.key, p.getPageNo(), p.getNumOfRows(), currentDate(), currentTime(), (int)p.getNx(), (int)p.getNy());
        log.info(uriString);
        try {
            String json = webClient.get()
                    .uri(new URI(uriString))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        log.error("Error status code: {}", clientResponse);
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException()));

                    })
                    .bodyToMono(String.class)
                    .block();

            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(json);
            JSONObject parseResponse = (JSONObject) obj.get("response");
            JSONObject parseBody = (JSONObject) parseResponse.get("body");
            JSONObject parseItems = (JSONObject) parseBody.get("items");
            parseItem = (JSONArray) parseItems.get("item");

        } catch (URISyntaxException e) {
            log.error("Invalid URI syntax: {}", uriString, e);
        } catch (JsonParseException e) {
            log.error("Error parsing JSON response from URI: {}", uriString, e);
        } catch (WebClientResponseException e) {
            log.error("Error response from web client: status code = {}, body = {}", e.getRawStatusCode(), e.getResponseBodyAsString(), e);
        } catch (IOException e) {
            log.error("I/O error occurred while fetching data from URI: {}", uriString, e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching forecast data", e);
        }

        return parseItem;
    }


    private String currentDate(){
        LocalDate now = LocalDate.now();

        return now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    private String currentTime(){
        LocalTime now = LocalTime.now();
        now = now.minusHours(1);

        return now.format(DateTimeFormatter.ofPattern("HH00"));
    }
}