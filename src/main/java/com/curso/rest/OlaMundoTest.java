package com.curso.rest;


import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


import java.util.Arrays;
import java.util.List;

public class OlaMundoTest {


    @Test
    public void testOlaMundo() {
        final Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/ola");
        Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
        Assert.assertTrue(response.statusCode() == 200);

        Assert.assertTrue("O status code deveria ser 200", response.statusCode() == 200);
        Assert.assertEquals(200, response.statusCode() == 200); //Obs: erro proposital, para passar o teste só comentar

        ValidatableResponse validaocao = response.then();
        validaocao.statusCode(200);
    }

    @Test
    public void devoConhecerOutrasFormasRestAssured() {
        final Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/ola");
        ValidatableResponse validaocao = response.then();
        validaocao.statusCode(200);

        get("http://restapi.wcaquino.me/ola").then().statusCode(200);


        given()
                //Pré condições
                .when() //Ação
                    .get("http://restapi.wcaquino.me/ola")
                .then()// Assertivas
//                    .assertThat()
                    .statusCode(200);

    }

    @Test
    public void devoConhecerMatchersHamcrest(){
        Assert.assertThat("Maria", Matchers.is("Maria"));
        Assert.assertThat(128, Matchers.is(128)); //Usando is quer dizer se a atual e igual a expectativa de validação
        Assert.assertThat(128, Matchers.isA(Integer.class)); //Usado quando o elemento e um inteiro Integer
        Assert.assertThat(128d, Matchers.isA(Double.class)); //Usado quando o elemento e um Double
        Assert.assertThat(128d, Matchers.greaterThan(120d)); //Usado quando o tanto de dias e maior que a expectativa
        Assert.assertThat(128d, Matchers.lessThan(130d)); //Usado quando o tanto de dias e menor que a expectativa

        List<Integer> impares = Arrays.asList(1,3,4,5,7,9);
        Assert.assertThat(impares, hasSize(5));
        Assert.assertThat(impares, contains(1,3,5,7,9));
        Assert.assertThat(impares, containsInAnyOrder(1,3,5,9,7));
        Assert.assertThat(impares, hasItem(1));
        Assert.assertThat(impares, hasItems(1, 5));

        Assert.assertThat("Maria", is(not("João")));
        Assert.assertThat("Maria", not("João"));
        Assert.assertThat("Joaquina", anyOf(is("João"), is("Joaquina")));
        Assert.assertThat("Joaquina", allOf(startsWith("João"), endsWith("ina"), containsString("aqui")));
    }

    @Test
    public void devoValidarBody() {

        given()
                .when()
                .get("http://restapi.wcaquino.me/ola")
                .then()
                .statusCode(200)
                .body(is("Ola Mundo!"))
                .body(containsString("Mundo"))
                .body(is(not(nullValue())));

    }
}
