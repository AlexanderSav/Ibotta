package com.ibotta.test;

import static io.restassured.RestAssured.given;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * 
 * @author Alexandr Savenkov
 * 
 * @see <a href=
 *      "https://www.testdevlab.com/blog/2018/06/an-introduction-to-testing-apis-using-rest-assured/">
 *      An introduction to testing api's using Rest Assured</a>
 *
 */
public class DictionaryAPITest {
	
	private static final String HOST = "http://localhost:3000";
	
	private static final String word = "abcde";

    @BeforeEach
    void init() throws Exception {
    	deleteWords();
    	postNewWords();
    }
    
    @AfterEach
    void tearDown() {
    	deleteWords();
    }
    
	/**
	 * POST /words
	 * add new words to the data store
	 */
    //included into each test
	//@Tag("main")
	//@Test
	public void postNewWords() throws Exception {
		String jsonTxt = IOUtils.toString(
	      this.getClass().getClassLoader().getResourceAsStream("post.json"),
	      "UTF-8"
	    );
		
		given().
			body(jsonTxt).
			header("Content-Type", "application/json").
		when().
			post(HOST + "/words").
		then().
			assertThat().
			statusCode(201);
	}
	
	/**
	 * DELETE /words 
	 * delete all contents of the data store
	 */
    //included into each test
	//@Test
	public void deleteWords() {
		given().
		when().
			delete(HOST + "/words").
		then().
			log().
			ifError().
			assertThat().
			statusCode(204);
	}
	
	/**
	 * GET /anagrams 
	 * get all anagrams from the data store
	 */
	@Tag("main")
	@Test
	public void getAllAnagarams() {
		given().
		when().
			get(HOST + "/anagrams").
		then().
			assertThat().
			statusCode(200);
	}
	
	/**
	 * GET /anagrams 
	 * get all anagrams from the data store, size is optional
	 */
	@Tag("main")
	@Test
	public void getAllAnagaramsWithSizeMoreOrEqualsThan() {
		int size = 1;
		given().
		when().
			get(HOST + "/anagrams?size=" + size).
		then().
			assertThat().
			statusCode(200).
			body("isEmpty()", Matchers.is(size<=1));
		
		size = 2;
		given().
		when().
			get(HOST + "/anagrams?size=" + size).
		then().
			assertThat().
			statusCode(200).
			body("anagrams[0].size()", Is.is(size));
	}
	
	/**
	 * GET /anagrams/{word} 
	 * get anagrams by word from the data store, limit is optional
	 * @return 
	 */
	@Tag("main")
	@Test
	public void getAnagramsByWord() {
		given().
		when().
			get(HOST + "/anagrams/" + word).
		then().
			assertThat().
			statusCode(200).
			body("anagrams.size()", Is.is(3));
	}
	
	/**
	 * GET /anagrams/{word} 
	 * get anagrams by word from the data store, limit is optional
	 */
	@Tag("main")
	@Test
	public void getAnagramsByWordWithLimit() {
		final int limit = 1;
		given().
		when().
			get(HOST + "/anagrams/" + word + "?limit=" + limit).
		then().
			assertThat().
			statusCode(200).
			body("anagrams.size()", Is.is(limit));
	}
	
	/**
	 * GET /anagrams/{word} 
	 * get anagrams by word from the data store, self is optional
	 */
	@Tag("main")
	@Test
	public void getAnagramsByWordWithFlagSelf() {
		final String self = "true";
		given().
		when().
			get(HOST + "/anagrams/" + word + "?self=" + self).
		then().
			assertThat().
			statusCode(200).
			body("anagrams.size()", Is.is(2));
	}
	
	/**
	 * GET /anagrams/{word} 
	 * get anagrams by word from the data store, self is optional
	 */
	@Tag("main")
	@Test
	public void getAnagramsByWordWithLimitAndFlagSelf() {
		final int limit = 1;
		final String self = "true";
		given().
		when().
			get(HOST + "/anagrams/" + word + "?self=" + self+"&limit=" + limit).
		then().
			assertThat().
			statusCode(200).
			body("anagrams.size()", Is.is(1));
	}

	/**
	 * DELETE /words/{word} 
	 * delete a single word from the data store
	 */
	@Tag("main")
	@Test
	public void deleteWord() {
		given().
		when().
			delete(HOST + "/words/" + word).
		then().
			log().
			ifError().
			assertThat().
			statusCode(204);
	}

	/**
	 * DELETE /anagrams/{word} 
	 * delete anagrams by word
	 */
	@Tag("optional")
	@Test
	public void deleteAnagramsByWord() {
		given().
		when().
			delete(HOST + "/anagrams/" + word).
		then().
			log().
			ifError().
			assertThat().
			statusCode(204);
	}
	
	/**
	 * GET /words/avg 
	 * get average word length in dictionary
	 */
	@Tag("optional")
	@Test
	public void getAverageWordLength() {
		given().
		when().
			get(HOST + "/words/avg").
		then().
			assertThat().
			statusCode(200).
			assertThat().
			body("result", Is.is(4));
	}
	
	/**
	 * GET /words/count 
	 * get count of words in dictionary
	 */
	@Tag("optional")
	@Test
	public void getCountOfWords() {
		given().
		when().
			get(HOST + "/words/count").
		then().
			assertThat().
			statusCode(200).
			assertThat().
			body("result", Is.is(11));
	}
	
	/**
	 * GET /words/max 
	 * get max word length in dictionary
	 */
	@Tag("optional")
	@Test
	public void getMaxWordLength() {
		given().
		when().
			get(HOST + "/words/max").
		then().
			assertThat().
			statusCode(200).
			assertThat().
			body("result", Is.is(6));
	}
	
	/**
	 * GET /words/min 
	 * get min word length in dictionary
	 */
	@Tag("optional")
	@Test
	public void getMinWordLength() {
		given().
		when().
			get(HOST + "/words/min").
		then().
			assertThat().
			statusCode(200).
			assertThat().
			body("result", Is.is(2));
	}
	
	/**
	 * GET /words/median 
	 * get median word length in dictionary
	 */
	@Tag("optional")
	@Test
	public void getMedianWordLength() {
		given().
		when().
			get(HOST + "/words/median").
		then().
			assertThat().
			statusCode(200).
			assertThat().
			body("result", Is.is(5));
	}
	
	/**
	 * GET /anagrams/most 
	 * get words with the most anagrams
	 */
	@Tag("optional")
	@Test
	public void getMostAnagrams() {
		given().
		when().
			get(HOST + "/anagrams/most").
		then().
			assertThat().
			statusCode(200).
			body("anagrams.size()", Is.is(4));
	}
	
	/**
	 * POST /anagrams/check 
	 * get 0-false or 1-true
	 * @throws IOException 
	 */
	@Tag("optional")
	@Test
	public void checkIfAllWordsAreInOneAnagramSet() throws IOException {
		String jsonTxt = IOUtils.toString(
	      this.getClass().getClassLoader().getResourceAsStream("check.json"),
	      "UTF-8"
	    );
		
		given().
			body(jsonTxt).
			header("Content-Type", "application/json").
		when().
			post(HOST + "/anagrams/check").
		then().
			assertThat().
			statusCode(200).
			body("result", Is.is(1));
	}
}
