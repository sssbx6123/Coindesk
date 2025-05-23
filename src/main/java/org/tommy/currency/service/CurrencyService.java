package org.tommy.currency.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tommy.currency.entity.Currency;
import org.tommy.currency.repository.CurrencyRepository;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    public List<Currency> findAll() {
        return currencyRepository.findAll();
    }

    public Optional<Currency> findById(String code) {
        return currencyRepository.findById(code);
    }

    public Currency save(Currency currency) {
        return currencyRepository.save(currency);
    }

    public boolean notExistsById(String code) {
        return !currencyRepository.existsById(code);
    }

    public void deleteById(String code) {
        currencyRepository.deleteById(code);
    }
}
