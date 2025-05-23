package org.tommy.currency.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class CoindeskServiceTest {

    private CoindeskService coindeskService;
    private RestTemplate restTemplateMock;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        restTemplateMock = mock(RestTemplate.class);
        objectMapper = new ObjectMapper();
        coindeskService = new CoindeskService() {
            // 覆寫 RestTemplate 與 ObjectMapper 以便注入 mock
            {
                try {
                    java.lang.reflect.Field restTemplateField = CoindeskService.class.getDeclaredField("restTemplate");
                    restTemplateField.setAccessible(true);
                    restTemplateField.set(this, restTemplateMock);

                    java.lang.reflect.Field objectMapperField = CoindeskService.class.getDeclaredField("objectMapper");
                    objectMapperField.setAccessible(true);
                    objectMapperField.set(this, objectMapper);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Test
    void testGetRawCoindesk_success() throws Exception {
        String json = "{\"time\":{\"updatedISO\":\"2025-06-01T12:00:00+00:00\"},\"bpi\":{\"USD\":{\"rate_float\":68000.0}}}";
        when(restTemplateMock.getForObject(anyString(), eq(String.class))).thenReturn(json);

        JsonNode result = coindeskService.getRawCoindesk();

        assertEquals("2025-06-01T12:00:00+00:00", result.path("time").path("updatedISO").asText());
        assertEquals(68000.0, result.path("bpi").path("USD").path("rate_float").asDouble());
    }

    @Test
    void testGetTransformedCoindesk_success() {
        String json = "{\"time\":{\"updatedISO\":\"2024-06-01T12:00:00+00:00\"},\"bpi\":{\"USD\":{\"rate_float\":68000.0},\"TWD\":{\"rate_float\":2000000.0}}}";
        when(restTemplateMock.getForObject(anyString(), eq(String.class))).thenReturn(json);

        Map<String, String> codeNameMap = new HashMap<>();
        codeNameMap.put("USD", "美元");
        codeNameMap.put("TWD", "新台幣");

        Map<String, Object> result = coindeskService.getTransformedCoindesk(codeNameMap);

        assertEquals("2024/06/01 20:00:00", result.get("updateTime"));
        List<Map<String, Object>> currencyList = (List<Map<String, Object>>) result.get("currencyList");
        assertEquals(2, currencyList.size());
        assertTrue(currencyList.stream().anyMatch(m -> "USD".equals(m.get("code")) && "美元".equals(m.get("name")) && (Double) m.get("rate") == 68000.0));
        assertTrue(currencyList.stream().anyMatch(m -> "TWD".equals(m.get("code")) && "新台幣".equals(m.get("name")) && (Double) m.get("rate") == 2000000.0));
    }
}
