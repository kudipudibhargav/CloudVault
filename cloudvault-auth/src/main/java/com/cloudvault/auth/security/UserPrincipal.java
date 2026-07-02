package com.cloudvault.auth.security;

import com.cloudvault.auth.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean twoFaEnabled;

    public UserPrincipal(UUID id, String email, String password, Collection<? extends GrantedAuthority> authorities, boolean twoFaEnabled) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.twoFaEnabled = twoFaEnabled;
    }

    public static UserPrincipal create(User user) {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().name())
        );

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                authorities,
                user.isTwoFaEnabled()
        );
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public boolean isTwoFaEnabled() {
        return twoFaEnabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String email;
        private String password;
        private Collection<? extends GrantedAuthority> authorities;
        private boolean twoFaEnabled;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder authorities(Collection<? extends GrantedAuthority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public Builder twoFaEnabled(boolean twoFaEnabled) {
            this.twoFaEnabled = twoFaEnabled;
            return this;
        }

        public UserPrincipal build() {
            return new UserPrincipal(id, email, password, authorities, twoFaEnabled);
        }
    }
}
