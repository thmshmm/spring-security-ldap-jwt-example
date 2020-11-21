package de.thmshmm.example.config;

public class ApiProperties {

    public static final String API_PREFIX = "/api";
    public static final String AUTH_API_PREFIX = API_PREFIX + "/auth";

    private ApiProperties() {
        throw new IllegalStateException("Static properties class");
    }
}
