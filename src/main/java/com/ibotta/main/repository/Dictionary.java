package com.ibotta.main.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

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
	
	public String getDictionaryKey(String word){
		if(word==null) return null;
		return word.chars().sorted().
		collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).
		toString();
	}
	
	//O(1)
	public Set<String> getAnagramSet(String word){
		return cache.get(this.getDictionaryKey(word));
	}
	
	//O(1)
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
	
	//O(n) n - count of anagrams and single words
	public int countOfWordsInDictionary(){
		return cache.values().stream().
		map(v->v.size()).
		reduce((s1, s2)->s1+s2).get();
	}
	
	//O(n) n - count of anagrams and single words
	public int maxWordLength(){
		return cache.keySet().stream().
		map(k->k.length()).
		max(Comparator.comparing(Integer::valueOf)).
        get();
	}
	
	//O(n) n - count of anagrams and single words
	public int minWordLength(){
		return cache.keySet().stream().
		map(k->k.length()).
		min(Comparator.comparing(Integer::valueOf)).
        get();
	}
	
	//O(n) n - count of anagrams and single words
	public long avgWordLength(){
		
		IntSummaryStatistics stats = 
			cache.keySet().stream().
			mapToInt(k->k.length()).
		    summaryStatistics();
		
		return Math.round(stats.getAverage());
	}
	
	//O(n*log(n)) n - count of anagrams and single words
	public int medianWordLength(){
		List<Integer> list = cache.keySet().stream().
		map(k->k.length()).
		sorted(Comparator.comparingInt(Integer::intValue)).
        collect(Collectors.toList());
		
		return list.get(Math.round(list.size()/2));
	}
}
