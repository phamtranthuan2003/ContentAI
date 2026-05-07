package com.suoidesign.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role implements GrantedAuthority {
    USER("USER"),
    ADMIN("ADMIN");
    
    private final String authority;
    
    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
