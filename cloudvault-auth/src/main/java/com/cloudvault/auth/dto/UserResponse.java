package com.cloudvault.auth.dto;

import java.util.UUID;

public class UserResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private boolean twoFaEnabled;

    public UserResponse() {
    }

    public UserResponse(UUID id, String email, String firstName, String lastName, String role, boolean twoFaEnabled) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.twoFaEnabled = twoFaEnabled;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isTwoFaEnabled() {
        return twoFaEnabled;
    }

    public void setTwoFaEnabled(boolean twoFaEnabled) {
        this.twoFaEnabled = twoFaEnabled;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String email;
        private String firstName;
        private String lastName;
        private String role;
        private boolean twoFaEnabled;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder twoFaEnabled(boolean twoFaEnabled) {
            this.twoFaEnabled = twoFaEnabled;
            return this;
        }

        public UserResponse build() {
            return new UserResponse(id, email, firstName, lastName, role, twoFaEnabled);
        }
    }
}
