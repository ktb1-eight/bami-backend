package com.example.bami.weather.service;

import com.example.bami.CacheLoggerService;
import com.example.bami.weather.dto.WeatherDTO;
import com.example.bami.weather.dto.WeatherResultDTO;
import io.netty.channel.ChannelOption;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final CacheLoggerService cacheLoggerService;
    private final WebClient webClient;
    private final String key;

    private static final String SEOUL_CITY_ADDRESS = "대한민국 서울특별시 중구 태평로1가";
    @Autowired
    public WeatherService(@Value("${weather.key}") String key, CacheLoggerService cacheLoggerService) {
        this.cacheLoggerService = cacheLoggerService;
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

    public double getTemparature(WeatherDTO q) {
        double fcstValue = 0;
        JSONArray parse_item = getForecast(q);

        if (parse_item == null) {
            log.error("Parsed forecast item is null, unable to retrieve temperature.");
            return fcstValue;
        }

        for (Object o : parse_item) {
            JSONObject weather = (JSONObject) o;
            if (!(weather.get("category")).equals("T1H")) {
                continue;
            }

            fcstValue = Double.parseDouble(weather.get("fcstValue").toString());
            break;
        }

        return fcstValue;
    }

    public double[] getHighLowTemperature(WeatherDTO q) {
        double low_val = 100;
        double high_val = 0;

        JSONArray parse_item = getForecast(q);

        // null 체크 추가
        if (parse_item == null) {
            log.error("Parsed forecast item is null, unable to retrieve high and low temperature.");
            return new double[] {low_val, high_val}; // 기본값 반환
        }

        JSONObject weather;

        for (Object o : parse_item) {
            weather = (JSONObject) o;
            if (!(weather.get("category")).equals("T1H")) continue;

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
                        this.key, p.getPageNo(), p.getNumOfRows(), currentDate(), currentTime(), p.getNx().intValue(), p.getNy().intValue());
        log.info(uriString);
        try {
            String json = webClient.get()
                    .uri(new URI(uriString))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        log.error("Error status code: {}", clientResponse.statusCode());
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("Failed to retrieve weather data: " + body)));
                    })
                    .bodyToMono(String.class)
                    .block();

            log.info("API Response: {}", json); // 응답 데이터 로그 기록

            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(json);

            if (obj == null) {
                log.error("Parsed JSON is null");
                return null;
            }

            JSONObject parse_response = (JSONObject) obj.get("response");
            if (parse_response == null) {
                log.error("Response object is null");
                return null;
            }

            JSONObject parse_body = (JSONObject) parse_response.get("body");
            if (parse_body == null) {
                log.error("Body object is null");
                return null;
            }

            JSONObject parse_items = (JSONObject) parse_body.get("items");
            if (parse_items == null) {
                log.error("Items object is null");
                return null;
            }

            parse_item = (JSONArray) parse_items.get("item");
            if (parse_item == null) {
                log.error("Items array is null");
            }

        } catch (Exception e) {
            log.error("Unexpected error while retrieving forecast data", e);
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

    @PostConstruct
    @Cacheable(value = "weatherCache", key = "'seoulCityWeather'")
    public WeatherResultDTO getSeoulCityWeather() {
        WeatherDTO seoulDTO = new WeatherDTO();
        seoulDTO.setNx(37.56570672735707);
        seoulDTO.setNy(126.97736222453338);

        double temperature = getTemparature(seoulDTO);
        double[] lowHighTemperature = getHighLowTemperature(seoulDTO);

        WeatherResultDTO result = WeatherResultDTO.builder()
                .cur_temperature(temperature)
                .low_temperature(lowHighTemperature[0])
                .high_temperature(lowHighTemperature[1])
                .city(SEOUL_CITY_ADDRESS)
                .status(HttpStatus.OK)
                .message("서울 시청의 날씨 정보입니다.")
                .build();

        log.info("Seoul City Weather has been cached.");
        cacheLoggerService.cacheAll();

        return result;
    }

    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    @CachePut(value = "weatherCache", key = "'seoulCityWeather'")
    public void updateSeoulCityWeather() {
        getSeoulCityWeather();
    }
}