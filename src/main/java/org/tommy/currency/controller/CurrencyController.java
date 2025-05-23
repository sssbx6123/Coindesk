package org.tommy.currency.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tommy.currency.entity.Currency;
import org.tommy.currency.service.CurrencyService;

@RestController
@RequestMapping("/api/currency")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @GetMapping
    public List<Currency> getAll() {
        return currencyService.findAll();
    }

    @GetMapping("/{code}")
    public ResponseEntity<Currency> getByCode(@PathVariable String code) {
        return currencyService.findById(code)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Currency create(@RequestBody Currency currency) {
        return currencyService.save(currency);
    }

    @PutMapping("/{code}")
    public ResponseEntity<Currency> update(@PathVariable String code, @RequestBody Currency currency) {
        if (currencyService.notExistsById(code)) {
            return ResponseEntity.notFound().build();
        }
        currency.setCode(code);
        return ResponseEntity.ok(currencyService.save(currency));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        if (currencyService.notExistsById(code)) {
            return ResponseEntity.notFound().build();
        }
        currencyService.deleteById(code);
        return ResponseEntity.noContent().build();
    }
}
