package com.ibotta.main.service;

import java.util.List;
import java.util.Optional;

import com.ibotta.main.model.AnagramModel;
import com.ibotta.main.model.ResultModel;
import com.ibotta.main.model.WordModel;

public interface IAnagaramService {
	
	List<AnagramModel> getAnagrams(Optional<Integer> size);
	
	AnagramModel getAnagramsByWord(String word, Optional<Long> limit, Optional<Boolean> self);
	
	void addWords(WordModel wordModel);
	
	boolean deleteWord(String word);
	
	void deleteWords();
	
	boolean deleteAnagramsByWord(String word);
	
	ResultModel countOfWordsInDictionary();
	
	ResultModel maxWordLength();
	
	ResultModel minWordLength();
	
	ResultModel avgWordLength();
	
	ResultModel medianWordLength();
	
	AnagramModel getMostAnagrams();
	
	ResultModel checkIfAllWordsAreInOneAnagramSet(WordModel wordModel);

}
