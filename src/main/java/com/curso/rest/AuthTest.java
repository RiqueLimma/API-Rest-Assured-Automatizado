package com.curso.rest;

import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.codehaus.groovy.syntax.Types.getText;
import static org.hamcrest.Matchers.*;

public class AuthTest {

    @Test
    public void deveAcessarSwaPI(){
        given()
                    .log().all()
                .when()
                    .get("https://swapi.dev/api/people/1")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("name", is("Luke Skywalker"))
        ;
    }
    //    https://api.openweathermap.org/data/2.5/weather?q=Fortaleza,BR&appid=7757c9fd9b4865165cbe1c87bac874d4&&units=metric
    @Test
    public void deveObterClima(){
        given()
                    .log().all()
                    .queryParam("q", "Fortaleza")
                    .queryParam("appid", "7757c9fd9b4865165cbe1c87bac874d4")
                    .queryParam("units", "metric")
//                    .contentType(ContentType.JSON)
//                    .header("Key", "7757c9fd9b4865165cbe1c87bac874d4")
//                    .body("{\n" +
//                        "   \"q\":\"Fortaleza\",\n" +
//                        "   \"appid\":\"7757c9fd9b4865165cbe1c87bac874d4\",\n" +
//                        "   \"units\":\"metric\"\n" +
//                        "}")
                .when()
                    .get("https://api.openweathermap.org/data/2.5/weather")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("name", is("Fortaleza"))
                    .body("coord.lon", is(-38.5247f))
                    .body("main.temp", greaterThan(25f))
                ;
    }

    @Test
    public void naoDeveAcessarSemSenha(){
        given()
                    .log().all()
                .when()
                    .get("https://restapi.wcaquino.me/basicauth")
                .then()
                    .log().all()
                    .statusCode(401)
        ;

    }

    @Test
    public void deveFazerAutenticacaoBasica(){
        given()
                    .log().all()
                .when()
                    .get("https://admin:senha@restapi.wcaquino.me/basicauth")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("status", is("logado"))
        ;

    }

    @Test
    public void deveFazerAutenticacaoBasica2(){
        given()
                    .log().all()
                    .auth().basic("admin", "senha")
                .when()
                    .get("https://restapi.wcaquino.me/basicauth")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("status", is("logado"))
        ;

    }

    @Test
    public void deveFazerAutenticacaoBasicaChallenge(){
        given()
                    .log().all()
                    .auth().preemptive().basic("admin", "senha")
                .when()
                    .get("https://restapi.wcaquino.me/basicauth2")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("status", is("logado"))
        ;

    }

    @Test
    public void deveFazerAutenticacaoComTokenJWT(){
        Map<String, String> login = new HashMap<String, String>();
        login.put("email", "luis@teste");
        login.put("senha", "123456");

        String token = given()
                .log().all()
                .body(login)
                .contentType(ContentType.JSON)
                .when()
                    .post("http://barrigarest.wcaquino.me/signin")
                .then()
                    .log().all()
                    .statusCode(200)
                    .extract().path("token")
        ;

        //Obter Contas
        given()
                    .log().all()
                    .header("Authorization", "JWT " + token)
                    .contentType(ContentType.JSON)
                .when()
                    .get("http://barrigarest.wcaquino.me/contas")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("nome", hasItem("Conta de teste"))
                ;
    }

    @Test
    public void deveAcessarAplicacaoWeb(){
        String cookie = given()
                    .log().all()
                    .formParam("email", "luis@teste")
                    .formParam("senha", "123456")
                    .contentType(ContentType.URLENC.withCharset("UTF=8"))
                .when()
                    .post("https://seubarriga.wcaquino.me/logar")
                .then()
                    .log().all()
                    .statusCode(200)
                    .extract().header("set-cookie");

        cookie = cookie.split("=")[1].split(";")[0];  // Linha serve para extrair o pesso que deseja
        System.out.println(cookie);

        //obter contas
        String body = given()
                    .log().all()
                    .cookie("connect.sid", cookie)
                .when()
                    .get("https://seubarriga.wcaquino.me/contas")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("html.body.table.tbody.tr[1].td[1]", is(notNullValue()))
                .extract().body().asString()
                ;
        System.out.println("----------------------------------------------");
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, body);
        System.out.println(xmlPath.getString("html.body.table.tbody.tr[1].td[1]"));
    }
}
