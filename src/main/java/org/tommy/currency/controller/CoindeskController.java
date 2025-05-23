package org.tommy.currency.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tommy.currency.entity.Currency;
import org.tommy.currency.repository.CurrencyRepository;
import org.tommy.currency.service.CoindeskService;

@RestController
@RequestMapping("/api/coindesk")
public class CoindeskController {

    @Autowired
    private CoindeskService coindeskService;

    @Autowired
    private CurrencyRepository currencyRepository;

    // 取得 coindesk 原始資料
    @GetMapping("/raw")
    public Object getRaw() {
        return coindeskService.getRawCoindesk();
    }

    // 取得轉換後的匯率資訊
    @GetMapping("/transformed")
    public Object getTransformed() {
        List<Currency> all = currencyRepository.findAll();
        Map<String, String> codeNameMap = all.stream().collect(Collectors.toMap(Currency::getCode, Currency::getName));
        return coindeskService.getTransformedCoindesk(codeNameMap);
    }
}
