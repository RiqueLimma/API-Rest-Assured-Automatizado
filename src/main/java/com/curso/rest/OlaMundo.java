package com.curso.rest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class OlaMundo {


    public static void main(String[] args) {
        final Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/ola");
        System.out.println(response.getBody().asString());
        System.out.println(response.statusCode());

        ValidatableResponse validaocao = response.then();
        validaocao.statusCode(200);
    }
}
