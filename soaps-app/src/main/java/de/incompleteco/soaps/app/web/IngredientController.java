package de.incompleteco.soaps.app.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.incompleteco.soaps.app.redis.Utilities;

/**
 * place to add "ingredients"
 * @author wschipp
 *
 */
@RestController
@RequestMapping("/app/ingredient")
public class IngredientController {

	@Autowired(required=false)
	private String ingredientKey = "ingredient:";
	
	@Autowired(required=false)
	private String wildcard = "*";
	
	@Autowired(required=false)
	private String keyLabel = "key";
	
	@Autowired
	private RedisTemplate<String,?> redisTemplate;
	
	@Autowired
	private Utilities utilities;
	
	@RequestMapping(method=RequestMethod.GET)
	public List<Map<?,?>> findAll() {
		//return
		return utilities.findAll(ingredientKey, keyLabel, redisTemplate);
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public void add(@RequestBody Map<String,String> ingredient){
		//check if it has the "key" key
		if (!ingredient.containsKey(keyLabel)) {
			//need to add it
			int keyCount = utilities.getKeyValue(ingredientKey, redisTemplate);
			//create the key
			ingredient.put(keyLabel, ingredientKey + keyCount);
		}//end if
		//persist
		redisTemplate.opsForHash().putAll(ingredient.get(keyLabel), ingredient);
	}
}
