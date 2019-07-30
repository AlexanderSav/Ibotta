package com.ibotta.main.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class AnagramService implements IAnagaramService {
	
	@Autowired
	private Dictionary dictionary;
	
	/**
	 * Getting all dictionary anagrams
	 * @param size to get all anagram groups of size >= X
	 * @return AnagramModel list
	 */
	public List<AnagramModel> getAnagrams(Optional<Integer> size){
		return dictionary.getDataStorage().entrySet().stream().map(e->{
			if((size.isPresent()&&size.get()>1&&e.getValue().size()>=size.get())||
					(!size.isPresent()&&e.getValue().size()>1)){
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
	 * @return AnagramModel
	 */
	public AnagramModel getAnagramsByWord(String word, Optional<Long> limit, Optional<Boolean> self){
		Set<String> anagramSet = dictionary.getAnagramSet(word);
		if(anagramSet!=null&&anagramSet.size()>1){

			AnagramModel anagram = new AnagramModel();
			
			Predicate<String> includeProperNouns = s ->(self.isPresent()&&self.get())?!s.equals(word):true;
			
			Stream<String>	stream = anagramSet.stream().filter(includeProperNouns);

			anagram.setAnagrams((limit.isPresent()?stream.limit(limit.get()):stream).toArray(n -> new String[n]));

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
	/**
	 * Removing anagrams by word in dictionary
	 * @param word in dictionary
	 */
	public boolean deleteAnagramsByWord(String word){
		return dictionary.deleteAnagramsByWord(word);
	}
	
	//Optional
	/**
	 * Getting count of words in dictionary
	 */
	public ResultModel countOfWordsInDictionary(){
		Collection<Set<String>> values = dictionary.getDataStorage().values();
		
		Number result = (values.size()<1)?0:
						values.stream().
						map(v->v.size()).
						reduce((s1, s2)->s1+s2).get();
		
		ResultModel res = new ResultModel();
		res.setResult(result);
		return res;
	}
	
	//Optional
	/**
	 * Getting max word length in dictionary
	 */
	public ResultModel maxWordLength(){
		Set<String> keys = dictionary.getDataStorage().keySet();
		
		Number result = (keys.size()<1)?0:
						keys.stream().
						map(k->k.length()).
						max(Comparator.comparing(Integer::valueOf)).
				        get();
		
		ResultModel res = new ResultModel();
		res.setResult(result);
		return res;
	}
	
	//Optional
	/**
	 * Getting min word length in dictionary
	 */
	public ResultModel minWordLength(){
		Set<String> keys = dictionary.getDataStorage().keySet();
		
		Number result = (keys.size()<1)?0:
						keys.stream().
						map(k->k.length()).
						min(Comparator.comparing(Integer::valueOf)).
				        get();

		ResultModel res = new ResultModel();
		res.setResult(result);
		return res;
	}
	
	//Optional
	/**
	 * Getting average word length in dictionary
	 */
	public ResultModel avgWordLength(){
		Set<String> keys = dictionary.getDataStorage().keySet();
		
		Number result = (keys.size()<1)?0:
				Math.round( 
						keys.stream().
						mapToInt(k->k.length()).
					    summaryStatistics().getAverage()
				);
		
		ResultModel res = new ResultModel();
		res.setResult(result);
		return res;
	}
	
	//Optional
	/**
	 * Getting median word length in dictionary
	 */
	public ResultModel medianWordLength(){
		Set<String> keys = dictionary.getDataStorage().keySet();
		
		List<Integer> list = keys.stream().
				map(k->k.length()).
				sorted(Comparator.comparingInt(Integer::intValue)).
		        collect(Collectors.toList());
		
		Number result = (keys.size()<1)?0:list.get(Math.round(list.size()/2));
		
		ResultModel res = new ResultModel();
		res.setResult(result);
		return res;
	}
	
	//Optional
	/**
	 * Getting most anagrams in dictionary
	 */
	public AnagramModel getMostAnagrams(){
		Set<String> anagramSet = dictionary.getDataStorage().entrySet().stream().
				filter(e->e.getValue().size()>1).
				reduce((e1, e2)->e1.getValue().size()>e2.getValue().size()?e1:e2).
				map(e->e.getValue()).
				get();
		
		AnagramModel anagram = new AnagramModel();
		anagram.setAnagrams(anagramSet.stream().toArray(n -> new String[n]));

		return anagram;
	}
	
	//Optional
	/**
	 * Check whether or not they are all anagrams of each other
	 * return 0 if any word is not dictionary
	 */
	public ResultModel checkIfAllWordsAreInOneAnagramSet(WordModel wordModel){
		ResultModel res = new ResultModel();
		res.setResult(dictionary.checkIfAllWordsAreInOneAnagramSet(wordModel.getWords())?1:0);
		return res;
	}
}
