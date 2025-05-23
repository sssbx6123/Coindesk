package org.tommy.currency.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tommy.currency.entity.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, String> {
}
