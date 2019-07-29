package com.ibotta.main.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ibotta.main.model.AnagramModel;
import com.ibotta.main.model.ResultModel;
import com.ibotta.main.model.WordModel;
import com.ibotta.main.service.AnagramService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Alexandr Savenkov
 * 
 * @see <br><a href="http://localhost:3000/swagger-ui.html">swagger ui</a>
 */
@RestController
@Api
public class AlexAPI {
	
	@Autowired
	AnagramService service;

	@ApiOperation(value = "get all anagrams from the data store, size is optional", response = List.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Anagrams retrieved. Note that a word is not considered to be its own anagram"),
			@ApiResponse(code = 500, message = "Internal server error"),
			@ApiResponse(code = 404, message = "Anagrams not found") })
	@RequestMapping(value = "/anagrams", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<AnagramModel>> getAnagrams(@RequestParam(value = "size") Optional<String> size) {
		Optional<Integer> intSize = Optional.empty();
		if(size.isPresent()){
			try{
				intSize = Optional.of(Integer.parseInt(size.get()));
			}catch(NumberFormatException e){
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
		} 
		return new ResponseEntity<List<AnagramModel>>(service.getAnagrams(intSize), HttpStatus.OK);
	}
	
	@ApiOperation(value = "get anagrams by word from the data store, limit is optional", response = AnagramModel.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Anagrams by word retrieved. Note that a word is not considered to be its own anagram"),
			@ApiResponse(code = 500, message = "Internal server error"),
			@ApiResponse(code = 400, message = "Bad request"),
			@ApiResponse(code = 404, message = "Anagrams by word not found") })
	@RequestMapping(value = "/anagrams/{word}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<AnagramModel> getAnagramsByWord(@PathVariable String word, 
			@RequestParam(value = "limit") Optional<String> limit,
			@RequestParam(value = "self") Optional<String> self) {

		Optional<Long> longLimit = Optional.empty();
		if(limit.isPresent()){
			try{
				longLimit = Optional.of(Long.parseLong(limit.get()));
			}catch(NumberFormatException e){
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
		} 
		Optional<Boolean> booleanSelf = Optional.empty();
		if(self.isPresent()){
			booleanSelf = Optional.of(Boolean.valueOf(
				("1".equalsIgnoreCase(self.get()) || "yes".equalsIgnoreCase(self.get()) || 
				        "true".equalsIgnoreCase(self.get()) || "on".equalsIgnoreCase(self.get()))?true:false
				));
		}
		return new ResponseEntity<AnagramModel>(service.getAnagramsByWord(word, longLimit, booleanSelf), HttpStatus.OK);
	}
	
	@ApiOperation(value = "add new words to the data store")
	@ApiResponses(value = { 
			@ApiResponse(code = 201, message = "Words were added"),
			@ApiResponse(code = 400, message = "Bad request"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@RequestMapping(value = "/words", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<Void> addWords(@RequestBody WordModel word) {
		service.addWords(word);
		return ResponseEntity.status(HttpStatus.CREATED).build();
    }
	
	@ApiOperation(value = "delete a single word from the data store")
	@ApiResponses(value = { 
			@ApiResponse(code = 204, message = "Word was deleted"),
			@ApiResponse(code = 400, message = "Bad request"),
			@ApiResponse(code = 404, message = "Not found a word in dictionary"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@RequestMapping(value = "/words/{word}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteWord(@PathVariable String word) {
		if(!service.deleteWord(word)){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
	
	@ApiOperation(value = "delete all contents of the data store")
	@ApiResponses(value = { 
			@ApiResponse(code = 204, message = "Words were deleted"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@RequestMapping(value = "/words", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteWords() {
		service.deleteWords();
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
	
	//Optional
	@ApiOperation(value = "delete anagrams by word")
	@ApiResponses(value = { 
			@ApiResponse(code = 204, message = "Words were deleted"),
			@ApiResponse(code = 404, message = "Not found a anagrams by word in dictionary"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@RequestMapping(value = "/anagrams/{word}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAnagramsByWord(@PathVariable String word) {
		if(!service.deleteAnagramsByWord(word)){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
	//Optional
	@ApiOperation(value = "get count of words in dictionary", response = ResultModel.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Count of words retrieved"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@RequestMapping(value = "/words/count", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<ResultModel> countOfWordsInDictionary(){
		return new ResponseEntity<ResultModel>(service.countOfWordsInDictionary(), HttpStatus.OK);
	}
	//Optional
	@ApiOperation(value = "get max word length in dictionary", response = ResultModel.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Max word length retrieved"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@RequestMapping(value = "/words/max", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<ResultModel> maxWordLength(){
		return new ResponseEntity<ResultModel>(service.maxWordLength(), HttpStatus.OK);
	}
	//Optional
	@ApiOperation(value = "get min word length in dictionary", response = ResultModel.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Min word length retrieved"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@RequestMapping(value = "/words/min", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<ResultModel> minWordLength(){
		return new ResponseEntity<ResultModel>(service.minWordLength(), HttpStatus.OK);
	}
	//Optional
	@ApiOperation(value = "get average word length in dictionary", response = ResultModel.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Average word length retrieved"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@RequestMapping(value = "/words/avg", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<ResultModel> avgWordLength(){
		return new ResponseEntity<ResultModel>(service.avgWordLength(), HttpStatus.OK);
	}
	//Optional
	@ApiOperation(value = "get median word length in dictionary", response = ResultModel.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Median word length retrieved"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@RequestMapping(value = "/words/median", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<ResultModel> medianWordLength(){
		return new ResponseEntity<ResultModel>(service.medianWordLength(), HttpStatus.OK);
	}
	//Optional
	@ApiOperation(value = "get words with the most anagrams", response = AnagramModel.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Words with the most anagrams"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@RequestMapping(value = "/anagrams/most", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<AnagramModel> getMostAnagrams(){
		return new ResponseEntity<AnagramModel>(service.getMostAnagrams(), HttpStatus.OK);
	} 
	//Optional
	@ApiOperation(value = "take a set of words and return whether or not they are all anagrams of each other")
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Set of words was analized"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@RequestMapping(value = "/anagrams/check", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<ResultModel> checkIfAllWordsAreInOneAnagramSet(@RequestBody WordModel word) {
		return new ResponseEntity<ResultModel>(service.checkIfAllWordsAreInOneAnagramSet(word), HttpStatus.OK);
    }
}
