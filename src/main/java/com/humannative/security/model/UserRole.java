package com.humannative.security.model;

import java.util.EnumSet;

public record UserRole(String userName, EnumSet<Role> roles) {
}
