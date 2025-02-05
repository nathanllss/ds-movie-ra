package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class MovieControllerRA {

	private String movieTitle;
	private String clientUsername, clientPassword, adminUsername, adminPassword;
	private String clientToken, adminToken, invalidToken;
	private Long existingId, nonExistingId;

	@BeforeEach
	void setUp() throws JSONException {
		baseURI = "http://localhost:8080";

		existingId = 1L;
		nonExistingId = 1000L;

		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";

		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		invalidToken = adminToken + "invalid";

	}
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {

		given()
		.when()
				.get("/movies")
		.then()
				.statusCode(200)
				.body("content", is(notNullValue()))
				.body("content", hasSize(20))
				.body("content.title", hasItems("The Witcher","Venom: Tempo de Carnificina"));
	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {
		movieTitle = "Venom: Tempo de Carnificina";

		given()
		.when()
				.get("/movies?title={movieTitle}", movieTitle)
		.then()
				.statusCode(200)
				.body("content", is(notNullValue()))
				.body("content", hasSize(1))
				.body("content.title[0]", equalTo(movieTitle));
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
		.when()
				.get("/movies/{id}", existingId)
		.then()
				.statusCode(200)
				.body("id", is(1))
				.body("title", equalTo("The Witcher"))
				.body("score", is(4.5f))
				.body("count", is(2))
				.body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
		.when()
				.get("/movies/{id}", nonExistingId)
		.then()
				.statusCode(404)
				.body("status", is(404))
				.body("error", equalTo("Recurso não encontrado"))
				.body("path", equalTo("/movies/"+nonExistingId.toString()));
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {		
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
	}
}
