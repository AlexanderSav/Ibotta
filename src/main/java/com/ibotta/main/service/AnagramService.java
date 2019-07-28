package com.ibotta.main.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibotta.main.model.AnagramModel;
import com.ibotta.main.model.ResultModel;
import com.ibotta.main.model.WordModel;
import com.ibotta.main.repository.Dictionary;

/**
 * @author Alexandr Savenkov
 *
 */
@Service
public class AnagramService {
	
	@Autowired
	Dictionary dictionary;
	
	/**
	 * Getting all dictionary anagrams
	 * @return List of anagrams
	 */
	public List<AnagramModel> getAnagrams(){
		return dictionary.getDataStorage().entrySet().stream().map(e->{
			if(e.getValue().size()>1){
				AnagramModel anagram = new AnagramModel();
				anagram.setAnagrams(e.getValue().stream().toArray(n -> new String[n]));
				return anagram;
			}
			//Note that a word is not considered to be its own anagram.
			return null;
		}).filter(Objects::nonNull).
		collect(Collectors.toList());
	}
	
	/**
	 * Getting dictionary anagram by word with limit
	 * @param word in dictionary
	 * @param limit to restrict anagram list
	 * @return Anagram
	 */
	public AnagramModel getAnagramsByWord(String word, Optional<Long> limit){
		Set<String> anagramSet = dictionary.getAnagramSet(word);
		if(anagramSet!=null&&anagramSet.size()>1){
			AnagramModel anagram = new AnagramModel();
			String[] result = limit.isPresent()?
			anagramSet.stream().limit(limit.get()).toArray(n -> new String[n]):
			anagramSet.stream().toArray(n -> new String[n]);
			anagram.setAnagrams(result);
			return anagram;
		}
		//Note that a word is not considered to be its own anagram.
		return null;
	}
	
	/**
	 * Adding words to dictionary
	 * @param word in dictionary
	 */
	public void addWords(WordModel wordModel){
		dictionary.addWords(wordModel.getWords());
	}
	
	/**
	 * Removing single word in dictionary
	 * @param word in dictionary
	 */
	public boolean deleteWord(String word){
		return dictionary.deleteWord(word);
	}
	
	/**
	 * Removing words in dictionary
	 */
	public void deleteWords(){
		dictionary.deleteWords();
	}
	
	
	//Optional
	public boolean deleteAnagramsByWord(String word){
		return dictionary.deleteAnagramsByWord(word);
	}
	//Optional
	public ResultModel countOfWordsInDictionary(){
		ResultModel res = new ResultModel();
		res.setResult(dictionary.countOfWordsInDictionary());
		return res;
	}
	//Optional
	public ResultModel maxWordLength(){
		ResultModel res = new ResultModel();
		res.setResult(dictionary.maxWordLength());
		return res;
	}
	//Optional
	public ResultModel minWordLength(){
		ResultModel res = new ResultModel();
		res.setResult(dictionary.minWordLength());
		return res;
	}
	//Optional
	public ResultModel avgWordLength(){
		ResultModel res = new ResultModel();
		res.setResult(dictionary.avgWordLength());
		return res;
	}
	//Optional
	public ResultModel medianWordLength(){
		ResultModel res = new ResultModel();
		res.setResult(dictionary.medianWordLength());
		return res;
	}
}
