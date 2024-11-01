package io.github.clamentos.grapher.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class RequestBodyFactory {

    public static final Map<String, String> requestBodies = Map.ofEntries(

        Map.entry("registerSuccessful_0", "{\"username\":\"TestUser\",\"password\":\"Password123?!\",\"email\":\"TestUser@nonexistent.com\",\"about\":\"I'm a testing user !\"}"),

        Map.entry("registerInvalidDto_0", "{\"username\":null,\"password\":\"Password123?!\"}"),
        Map.entry("registerInvalidDto_1", "{\"username\":\"TestUser\",\"password\":null}"),
        Map.entry("registerInvalidDto_2", "{\"username\":\"TestUserNotExists\",\"password\":\"pwd1\"}"),

        Map.entry("loginSuccessful_0", "{\"username\":\"TestUser\",\"password\":\"Password123?!\"}"),
        Map.entry("loginUserNotFound_0", "{\"username\":\"TestUserNotExists\",\"password\":\"Password123?!\"}"),
        Map.entry("loginWrongPassword_0", "{\"username\":\"TestUser\",\"password\":\"Password124?!\"}"),

        Map.entry("loginInvalidDto_0", "{\"username\":null,\"password\":\"Password124?!\"}")
    );

    public static Stream<String> supplier(String key) {

        List<String> bodies = new ArrayList<>();

        for(Map.Entry<String, String> body : requestBodies.entrySet()) {

            if(body.getKey().startsWith(key)) {

                bodies.add(body.getValue());
            }
        }

        return(bodies.stream());
    }
}
