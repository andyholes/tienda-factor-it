package com.factorit.challenge.model.payload;

public record ClientUserResponse(
        Long id,
        String name,
        String lastName,
        boolean vip
){}
