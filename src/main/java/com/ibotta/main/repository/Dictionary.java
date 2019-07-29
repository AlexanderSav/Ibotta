package com.ibotta.main.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Savenkov
 *
 */
@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Dictionary {
	
	private final String dicFileName = "dictionary.txt";
	
	private Map<String, Set<String>> cache;
	
	public Map<String, Set<String>> getDataStorage(){
		return cache;
	}

	@PostConstruct
    public void init() {
        cache = new ConcurrentHashMap<String, Set<String>>();
        
        try (BufferedReader br =  new BufferedReader(new InputStreamReader(
                this.getClass().getResourceAsStream("/" + dicFileName)))) {

			String word;
			while ((word = br.readLine()) != null) {
				String key = this.getDictionaryKey(word);
	        	Set<String> set = cache.get(key);
	        	if(set==null){
	        		cache.put(key,new CopyOnWriteArraySet<String>());
	        	}
	        	cache.get(key).add(word);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	//O(n*log(n)) n - word length
	private String getDictionaryKey(String word){
		if(word==null) return null;
		return word.chars().sorted().
		collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).
		toString();
	}
	
	//O(n*log(n)) n - word length
	public Set<String> getAnagramSet(String word){
		//retrieving anagrams, word can be out of set
		return cache.get(this.getDictionaryKey(word));
	}
	
	//O(m*n*log(n)) m - words array size, n - word length
	public void addWords(String...words){
		Arrays.stream(words).forEach(
			word->{
				String key = this.getDictionaryKey(word);
				Set<String> set = cache.get(key);
				if(set==null){
					set = new CopyOnWriteArraySet<String>();
				}
				set.add(word);
				cache.put(key, set);
			}
		);
	}
	
	//O(1)
	public boolean deleteWord(String word){
		String key = this.getDictionaryKey(word);
		Set<String> set = cache.get(key);
		if(set!=null&&set.contains(word)){
			set.remove(word);
			return true;
		}
		return false;
	}
	
	//O(1)
	public void deleteWords(){
		cache = new ConcurrentHashMap<String, Set<String>>();
	}
	
	//Optional
	
	//O(1)
	public boolean deleteAnagramsByWord(String word){
		String key = this.getDictionaryKey(word);
		Set<String> set = cache.get(key);
		//Note that a word is not considered to be its own anagram.
		if(set!=null&&set.size()>1){
			cache.remove(key);
			return true;
		}
		return false;
	}
	
	//O(n) n - words array size
	public boolean checkIfAllWordsAreInOneAnagramSet(String...words){
		if(words.length<1) return false;
		
		if(Arrays.stream(words).
			map(this::getDictionaryKey).
			distinct().
			count()>1) return false;
		
		String key = this.getDictionaryKey(words[0]);
		
		Set<String> set = cache.get(key);
		if(set!=null&&set.size()>1){
			return Arrays.stream(words).
			filter(s->!set.contains(s)).
			count()==0;
		}
		return false;
	}
}
