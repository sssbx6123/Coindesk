package org.tommy.currency.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CoindeskService {

    private static final String COINDESK_URL = "https://kengp3.github.io/blog/coindesk.json";

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    public CoindeskService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public JsonNode getRawCoindesk() {
        String json = restTemplate.getForObject(COINDESK_URL, String.class);
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("解析 coindesk 回應失敗", e);
        }
    }

    public Map<String, Object> getTransformedCoindesk(Map<String, String> codeNameMap) {
        JsonNode raw = getRawCoindesk();
        Map<String, Object> result = new HashMap<>();
        // 取得時間
        String updateTime = raw.path("time").path("updatedISO").asText();
        String formattedTime = formatTime(updateTime);
        result.put("updateTime", formattedTime);
        // 幣別資訊
        List<Map<String, Object>> currencyList = new ArrayList<>();
        JsonNode bpi = raw.path("bpi");
        bpi.fieldNames().forEachRemaining(code -> {
            JsonNode node = bpi.get(code);
            Map<String, Object> info = new HashMap<>();
            info.put("code", code);
            info.put("name", codeNameMap.getOrDefault(code, ""));
            info.put("rate", node.path("rate_float").asDouble());
            currencyList.add(info);
        });
        result.put("currencyList", currencyList);
        return result;
    }

    private String formatTime(String isoTime) {
        try {
            SimpleDateFormat isoFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            Date date = isoFmt.parse(isoTime);
            SimpleDateFormat targetFmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return targetFmt.format(date);
        } catch (Exception e) {
            return isoTime;
        }
    }
}
