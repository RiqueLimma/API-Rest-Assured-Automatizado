package com.curso.rest;

import io.restassured.http.ContentType;
import org.junit.Assert;
import org.junit.Test;


import javax.jws.soap.SOAPBinding;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class VerboTest {

    @Test //POST
    public void deveSalvarUsuario(){
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "        \"name\": \"Luis\",\n" +
                        "        \"age\": 30\n" +
                        "    }")
                .when()
                    .post("https://restapi.wcaquino.me/users")
                .then()
                    .log().all()
                    .statusCode(201)
                    .body("id", is(notNullValue()))
                    .body("name", is("Luis"))
                    .body("age", is(30))
                ;
    }


    @Test //POST
    public void naoDeveSalvarUsuarioSemNome(){
        given()
                    .log().all()
                    .contentType(ContentType.JSON)
                    .body("{\n" +
                        "        \"age\": 30\n" +
                        "    }")
                .when()
                    .post("https://restapi.wcaquino.me/users")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("id", is(nullValue()))
                    .body("error", is("Name é um atributo obrigatório"))
        ;
    }

    @Test //POST
    public void deveSalvarUsuarioXML(){
        given()
                .log().all()
                .contentType(ContentType.XML)
                .body("<user>" +
                            "<name>Luis</name>" +
                            "<age>30</age>" +
                        "</user>")
                .when()
                    .post("https://restapi.wcaquino.me/usersXML")
                .then()
                    .log().all()
                    .statusCode(201)
                    .body("user.@id", is(notNullValue()))
                    .body("user.name", is("Luis"))
                    .body("user.age", is("30"))
        ;
    }

    @Test //PUT
    public void deveAlterarUsuario(){
        given()
                    .log().all()
                    .contentType(ContentType.JSON)
                    .body("{\n" +
                        "        \"name\": \"Chiquinho\",\n" +
                        "        \"age\": 20\n" +
                        "    }")
                .when()
                    .put("https://restapi.wcaquino.me/users/1")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("name", is("Chiquinho"))
                    .body("age", is(20))
                    .body("salary", is(1234.5678f))
        ;
    }

    @Test //PUT
    public void custumizarURL1(){
        given()
                    .log().all()
                    .contentType(ContentType.JSON)
                .body("{\n" +
                        "        \"name\": \"Chiquinho\",\n" +
                        "        \"age\": 20\n" +
                        "    }")
                .when()
                    .put("https://restapi.wcaquino.me/{entidade}/{userId}", "users", "1")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("name", is("Chiquinho"))
                    .body("age", is(20))
                    .body("salary", is(1234.5678f))
        ;
    }

    @Test //PUT
    public void custumizarURL2(){
        given()
                    .log().all()
                    .contentType(ContentType.JSON)
                    .body("{\n" +
                        "        \"name\": \"Chiquinho\",\n" +
                        "        \"age\": 20\n" +
                        "    }")
                    .pathParam("entidade", "users")
                    .pathParam("userId", 1)
                .when()
                    .put("https://restapi.wcaquino.me/{entidade}/{userId}")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("name", is("Chiquinho"))
                    .body("age", is(20))
                    .body("salary", is(1234.5678f))
        ;
    }

    @Test //Delete
    public void deveRemoverUsuario(){
        given()
                    .log().all()
                .when()
                    .delete("https://restapi.wcaquino.me/users/1")
                .then()
                    .log().all()
                    .statusCode(204)
        ;
    }

    @Test //Delete
    public void naoDeveRemoverUsuarioInesxistente(){
        given()
                    .log().all()
                .when()
                    .delete("https://restapi.wcaquino.me/users/1000")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("error", is("Registro inexistente"))
        ;
    }

    @Test //POST
    public void deveSalvarUsuarioMap() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "Usuario via map");
        params.put("age", 20);

        given()
                    .log().all()
                    .contentType(ContentType.JSON)
                    .body(params)
                .when()
                    .post("https://restapi.wcaquino.me/users")
                .then()
                    .log().all()
                    .statusCode(201)
                    .body("id", is(notNullValue()))
                    .body("name", is("Usuario via map"))
                    .body("age", is(20))
        ;


    }

    @Test //POST
    public void deveSalvarUsuarioObjeto() {
        User user = new User("Usuario via objeto", 20);

        given()
                    .log().all()
                    .contentType(ContentType.JSON)
                    .body(user)
                .when()
                    .post("https://restapi.wcaquino.me/users")
                .then()
                    .log().all()
                    .statusCode(201)
                    .body("id", is(notNullValue()))
                    .body("name", is("Usuario via objeto"))
                    .body("age", is(20))
        ;
    }

    @Test //POST
    public void deveDeserializarObjetoAoSalvarUsuario() {
        User user = new User("Usuario deserializado", 20);

        User usuarioInserido = given()
                    .log().all()
                    .contentType(ContentType.JSON)
                    .body(user)
                .when()
                    .post("https://restapi.wcaquino.me/users")
                    .then()
                    .log().all()
                    .statusCode(201)
                    .extract().body().as(User.class);

        System.out.println(usuarioInserido);
        Assert.assertThat(usuarioInserido.getId(), notNullValue());
        Assert.assertEquals("Usuario deserializado", usuarioInserido.getName());
        Assert.assertThat(usuarioInserido.getAge(), is(20));
    }

    @Test //POST
    public void deveSalvarUsuarioViaXMLUsandoObjeto() {
        User user = new User("Usuario XML", 20);

        given()
                    .log().all()
                    .contentType(ContentType.XML)
                    .body(user)
                .when()
                    .post("https://restapi.wcaquino.me/usersXML")
                .then()
                    .log().all()
                    .statusCode(201)
                    .body("user.@id", is(notNullValue()))
                    .body("user.name", is("Usuario XML"))
                    .body("user.age", is("20"))
                ;
    }

    @Test //POST
    public void deveDeserializarXMLAoSalvarUsuarioViaXMLUsandoObjeto() {
        User user = new User("Usuario XML deserializar", 20);

       User usuarioInserido = given()
                    .log().all()
                    .contentType(ContentType.XML)
                    .body(user)
                .when()
                    .post("https://restapi.wcaquino.me/usersXML")
                .then()
                    .log().all()
                    .statusCode(201)
                    .extract().body().as(User.class)
        ;
        System.out.println(usuarioInserido);
        Assert.assertThat(usuarioInserido.getId(), notNullValue());
        Assert.assertThat(usuarioInserido.getName(), is("Usuario XML deserializar"));
        Assert.assertThat(usuarioInserido.getAge(), is(20));
        Assert.assertThat(usuarioInserido.getSalary(), nullValue());

    }


}

