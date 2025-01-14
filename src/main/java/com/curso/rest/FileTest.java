package com.curso.rest;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class FileTest {

    @Test
    public void deveObrigarEnviarArquivo() {
        given()
                    .log().all()
                .when()
                    .post("https://restapi.wcaquino.me/upload")
                .then()
                    .log().all()
                    .statusCode(404)
                    .body("error", is("Arquivo não enviado"))
        ;

    }

    @Test
    public void deveFazerUploadArquivo() {
        given()
                .log().all()
                .multiPart("arquivo", new File("src/main/resources/user.pdf"))
                .when()
                    .post("https://restapi.wcaquino.me/upload")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("name", is("user.pdf"))
        ;

    }

    @Test
    public void naoDeveFazerUploadArquivoGrande() {
        given()
                    .log().all()
                    .multiPart("arquivo", new File("src/main/resources/teste.mkv"))
                .when()
                   .post("https://restapi.wcaquino.me/upload")
                .then()
                    .log().all()
                    .time(lessThan(5000L))
                    .statusCode(413)
        ;

    }

    @Test
    public void deveBaixarArquivo() throws IOException {
       byte[] image = given()
                    .log().all()
                .when()
                    .get("https://restapi.wcaquino.me/download")
                .then()
//                    .log().all()
                    .statusCode(200)
                    .extract().asByteArray()
               ;
       File imagem = new File("src/main/resources/file.jpg");
        OutputStream out = new FileOutputStream(imagem);
        out.write(image);
        out.close();

        System.out.println(imagem.length());
        Assert.assertThat(imagem.length(), lessThan(100000L));
    }
}
