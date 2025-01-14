package com.curso.rest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;


import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserJsonTest {

    @Test
    public void devoVerificarPrimeiroNivel() {

        given()
                .when()
                  .get("https://restapi.wcaquino.me/users/1")
                .then()
                  .statusCode(200)
                  .body("id", is(1))
                  .body("name", containsString("Silva"))
                  .body("age ", greaterThan(18));
    }

    @Test
    public void devoVerificarPrimeiroNivelOutrasFormas() {
        Response response = RestAssured.request(Method.GET, "https://restapi.wcaquino.me/users/1");

        Assert.assertEquals(new Integer(1), response.path("id"));
        Assert.assertEquals(new Integer(1), response.path("%s", "id"));
    }

    @Test
    public void devoVerificarSegundoNivelOutrasFormas() {
        given()
                .when()
                    .get("https://restapi.wcaquino.me/users/2")
                .then()
                    .statusCode(200)
                    .body("name", containsString("Joaquina"))
                    .body("endereco.numero", is(0));
    }

    @Test
    public void devoVerificarLista() {
        given()
                .when()
                    .get("https://restapi.wcaquino.me/users/3")
                .then()
                    .statusCode(200)
                    .body("name", containsString("Ana"))
                    .body("filhos", hasSize(2))
                    .body("filhos[0].name", is("Zezinho"))
                    .body("filhos[1].name", is("Luizinho"))
                    .body("filhos.name", hasItem("Zezinho"))
                    .body("filhos.name", hasItems("Zezinho", "Luizinho"));

    }

    @Test
    public void deveRetornaErroUsuarioIniesxistente() {
        given()
                .when()
                    .get("https://restapi.wcaquino.me/users/4")
                .then()
                    .statusCode(404)
                    .body("error", is("Usuário inexistente"));
    }

    @Test
    public void deveVerificarListaRaiza() {
        given()
                .when()
                    .get("https://restapi.wcaquino.me/users")
                .then()
                    .statusCode(200)
                    .body("$", hasSize(3))
                    .body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
                    .body("age[1]", is(25))
                    .body("filhos.name", hasItem(Arrays.asList("Zezinho", "Luizinho")))
                    .body("salary", contains(1234.5677f, 2500, null))

        ;
    }


    @Test
    public void devoVerificarAcoesAvancadas() {
        given()
                .when()
                    .get("https://restapi.wcaquino.me/users")
                .then()
                    .statusCode(200)
                    .body("$", hasSize(3))
                    .body("age.findAll{it <= 25}.size()", is(2))
                    .body("age.findAll{it <= 25 && it > 20}.size()", is(1))
                    .body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina"))
                    .body("findAll{it.age <= 25}[0].name", is("Maria Joaquina"))
                    .body("findAll{it.age <= 25}[-1].name", is("Ana Júlia"))
                    .body("find{it.age <= 25}.name", is("Maria Joaquina"))
                    .body("findAll{it.name.contains ('n')}.name", hasItems("Maria Joaquina", "Ana Júlia"))
                    .body("findAll{it.name.length() > 10}.name", hasItems("João da Silva", "Maria Joaquina"))
                    .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("MARIA JOAQUINA"), arrayWithSize(1)))
                    .body("age.collect{it * 2}", hasItems(60, 50, 40))
                    .body("id.max()", is(3))
                    .body("salary.min()", is(1234.5677f))
                    .body("salary.findAll{it != null}.sum()", is(closeTo(3734.5677f, 0.001)))
                    .body("salary.findAll{it != null}.sum()", allOf(greaterThan(3000d), lessThan(5000d)));
    }

    @Test
    public void devoUnirJsonPathComJava() {
        ArrayList<String> names =
        given()
                .when()
                    .get("https://restapi.wcaquino.me/users")
                .then()
                    .statusCode(200)
                .extract().path("name.findAll{it.startsWith('Maria')}");

        Assert.assertEquals(1, names.size());
        Assert.assertTrue(names.get(0).equalsIgnoreCase("mAria Joaquina"));
        Assert.assertEquals(names.get(0).toUpperCase(), "maria joaquina".toUpperCase());
    }


}
