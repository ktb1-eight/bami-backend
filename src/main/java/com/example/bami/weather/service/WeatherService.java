package com.example.bami.weather.service;

import com.example.bami.weather.dto.WeatherDTO;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


@Service
@Slf4j
public class WeatherService {
    private final WebClient webClient;
    private final String key;

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

        JSONArray parse_item = getForecast(q);
        JSONObject weather; // parse_item은 배열형태이기 때문에 하나씩 데이터를 하나씩 가져올때 사용

        for (Object o : parse_item) {
            weather = (JSONObject) o;
            if (!(weather.get("category")).equals("T1H")) {
                continue;
            }

            Object fcstDate = weather.get("fcstDate");
            Object fcstTime = weather.get("fcstTime");
            fcstValue = Double.parseDouble(weather.get("fcstValue").toString());

            log.info("\tcategory : " + weather.get("category") +
                    ", fcst_Value : " + fcstValue +
                    ", fcstDate : " + fcstDate +
                    ", fcstTime : " + fcstTime);
            break;
        }


        return fcstValue;
    }

    public double[] getHighLowTemperature(WeatherDTO q){
        double low_val = 100;
        double high_val = 0;

        JSONArray parse_item = getForecast(q);
        JSONObject weather; // parse_item은 배열형태이기 때문에 하나씩 데이터를 하나씩 가져올때 사용

        for (Object o : parse_item) {
            weather = (JSONObject) o;
            if (!(weather.get("category")).equals("T1H")) {
                continue;
            }

            double fcst_val = Double.parseDouble(weather.get("fcstValue").toString());

            low_val = Math.min(fcst_val, low_val);
            high_val = Math.max(fcst_val, high_val);
            log.info(weather.get("fcstTime") + " " + weather.get("fcstDate") + " " + fcst_val);
        }


        return new double[] {low_val, high_val};
    }
    private JSONArray getForecast(WeatherDTO p) {
        JSONArray parse_item = null;

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
            JSONObject parse_response = (JSONObject) obj.get("response");
            JSONObject parse_body = (JSONObject) parse_response.get("body");
            JSONObject parse_items = (JSONObject) parse_body.get("items");
            parse_item = (JSONArray) parse_items.get("item");

        } catch ( Exception e) {
            e.printStackTrace();
            log.error("Unexpected error 111", e);
        }

        return parse_item;
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