package com.factorit.challenge.service;

import com.factorit.challenge.model.payload.ClientUserResponse;

import java.time.YearMonth;
import java.util.List;

public interface ClientUserService {
    List<ClientUserResponse> getAll(Boolean vip, YearMonth monthOfUpdate);
    ClientUserResponse getById(Long id);
}
