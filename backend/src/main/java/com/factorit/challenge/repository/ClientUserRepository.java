package com.factorit.challenge.repository;

import com.factorit.challenge.model.ClientUser;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientUserRepository extends JpaRepository<ClientUser, Long> {

    List<ClientUser> findAll(Specification<ClientUser> specification);
}
