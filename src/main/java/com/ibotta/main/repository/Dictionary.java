package com.ibotta.main.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
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
	
	private Map<String, Set<String>> initCache(){
		
		/*
		 * The HashMap implementation provides constant time performance for (get and put) basic operations 
		 * i.e the complexity of get() and put() is O(1) , assuming the hash function disperses the elements 
		 * properly among the buckets. 
		 */
		//return new ConcurrentHashMap<String, Set<String>>();
		
		
		/*
		 * Treemap(ConcurrentSkipListMap) is sorted according to the natural ordering of its keys, or by a 
		 * Comparator provided at map creation time, depending on which constructor is used. This implementation 
		 * provides guaranteed log(n)time cost for the containsKey, get, put and remove operations. 
		 * For search operation,in java 7,it will take O(n) with a linked list. 
		 * While in java 8 , the same search operation in a tree will cost O(log(n)).
		 */
		return new ConcurrentSkipListMap<String, Set<String>>();
	}
	
	private Set<String> initAnagramGroup(){
		return new CopyOnWriteArraySet<String>();
	}

	@PostConstruct
    public void init() {
        cache = initCache();
        
        try (BufferedReader br =  new BufferedReader(new InputStreamReader(
                this.getClass().getResourceAsStream("/" + dicFileName)))) {

			String word;
			while ((word = br.readLine()) != null) {
				String key = this.getDictionaryKey(word);
	        	Set<String> set = cache.get(key);
	        	if(set==null){
	        		cache.put(key, initAnagramGroup());
	        	}
	        	cache.get(key).add(word);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	private String getDictionaryKey(String word){
		if(word==null) return null;
		return word.chars().sorted().
		collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).
		toString();
	}

	public Set<String> getAnagramSet(String word){
		//retrieving anagrams, word can be out of set
		return cache.get(this.getDictionaryKey(word));
	}

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

	public boolean deleteWord(String word){
		String key = this.getDictionaryKey(word);
		Set<String> set = cache.get(key);
		if(set!=null&&set.contains(word)){
			set.remove(word);
			return true;
		}
		return false;
	}

	public void deleteWords(){
		cache = initCache();
	}
	
	//Optional

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
