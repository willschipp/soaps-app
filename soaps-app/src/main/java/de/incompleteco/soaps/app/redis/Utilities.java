package de.incompleteco.soaps.app.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * reusable stuff for redis (quasi-DAO layer)
 * @author wschipp
 *
 */
public class Utilities {
	
	@Autowired(required=false)
	private String wildcard = "*";

	public int getKeyValue(String keyPrefix,RedisTemplate<String,?> redisTemplate) {
		//need to add it
		int keyCount = 0;
		Set<String> keys = redisTemplate.keys(keyPrefix + wildcard);
		if (keys != null) {
			keyCount = keys.size();
		}//end if
		return keyCount;
	}
	
	public List<Map<?,?>> findAll(String keyPrefix,String keyLabel,RedisTemplate<String,?> redisTemplate) {
		List<Map<?,?>> results = new ArrayList<Map<?,?>>();
		Set<String> keys = redisTemplate.keys(keyPrefix + wildcard);
		for (String key : keys) {
			Map<Object,Object> result = redisTemplate.opsForHash().entries(key);
			result.put(keyLabel,key);
			results.add(result);
		}//end for
		//return
		return results;
	}	
	
}
