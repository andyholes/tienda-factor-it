package com.factorit.challenge.repository;

import com.factorit.challenge.model.SpecialDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialDateRepository extends JpaRepository<SpecialDate, Long> {
}
