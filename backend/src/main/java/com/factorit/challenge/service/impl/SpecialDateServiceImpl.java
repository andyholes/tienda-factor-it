package com.factorit.challenge.service.impl;

import com.factorit.challenge.model.SpecialDate;
import com.factorit.challenge.repository.SpecialDateRepository;
import com.factorit.challenge.service.SpecialDateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialDateServiceImpl implements SpecialDateService {
    private final SpecialDateRepository dateRepository;
    @Override
    public List<LocalDate> getAll() {
        int currentYear = LocalDate.now().getYear();
        return dateRepository.findAll().stream()
                .map(specialDate -> toLocalDate(currentYear, specialDate)).toList();
    }

    private LocalDate toLocalDate (int currentYear, SpecialDate specialDate){
        MonthDay date = specialDate.getDate();
        return LocalDate.of(currentYear,date.getMonth(),date.getDayOfMonth());
    }
}
