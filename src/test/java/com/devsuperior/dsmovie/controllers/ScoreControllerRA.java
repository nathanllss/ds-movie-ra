package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class ScoreControllerRA {

	private Long existingMovieId, nonExistingMovieId;
	private String clientUsername, clientPassword, clientToken;
	private Map<String, Object> postScoreInstance;

	@BeforeEach
	void setUp() throws Exception {
		baseURI = "http://localhost:8080";

		existingMovieId = 1L;
		nonExistingMovieId = 1000L;

		clientUsername = "alex@gmail.com";
		clientPassword = "123456";

		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);

		postScoreInstance = new HashMap<>();
		postScoreInstance.put("movieId", existingMovieId);
		postScoreInstance.put("score", 4);
	}
	
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {
		postScoreInstance.put("movieId", nonExistingMovieId);
		JSONObject scoreBody = new JSONObject(postScoreInstance);

		given()
				.header("Authorization", "Bearer " + clientToken)
				.body(scoreBody)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
		.when()
				.put("/scores")
		.then()
				.statusCode(404)
				.body("status", is(404))
				.body("error", equalTo("Recurso não encontrado"))
				.body("path", equalTo("/scores"));
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		postScoreInstance.put("movieId", null);
		JSONObject scoreBody = new JSONObject(postScoreInstance);

		given()
				.header("Authorization", "Bearer " + clientToken)
				.body(scoreBody)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
		.when()
				.put("/scores")
		.then()
				.statusCode(422)
				.body("status", is(422))
				.body("error", equalTo("Dados inválidos"))
				.body("path", equalTo("/scores"))
				.body("errors.fieldName[0]", equalTo("movieId"))
				.body("errors.message[0]", equalTo("Campo requerido"));

	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
		postScoreInstance.put("score", -1);
		JSONObject scoreBody = new JSONObject(postScoreInstance);

		given()
				.header("Authorization", "Bearer " + clientToken)
				.body(scoreBody)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
		.when()
				.put("/scores")
		.then()
				.statusCode(422)
				.body("status", is(422))
				.body("error", equalTo("Dados inválidos"))
				.body("path", equalTo("/scores"))
				.body("errors.fieldName[0]", equalTo("score"))
				.body("errors.message[0]", equalTo("Valor mínimo 0"));
	}
}
