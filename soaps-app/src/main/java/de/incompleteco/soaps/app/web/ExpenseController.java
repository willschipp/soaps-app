package de.incompleteco.soaps.app.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.incompleteco.soaps.app.redis.Utilities;

/**
 * primary expense controller
 * @author wschipp
 *
 */
@RestController
@RequestMapping("/app/expense")
public class ExpenseController {
	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(ExpenseController.class);

	@Autowired(required=false)
	private String expenseKey = "expense:";	
	
	@Autowired(required=false)
	private String wildcard = "*";	
	
	@Autowired(required=false)
	private String keyLabel = "key";	
	
	@Autowired(required=false)
	private String amountKey = "amount";
	
	@Autowired(required=false)
	private String currencyKey = "currency";	
	
	@Autowired
	private RedisTemplate<String,?> redisTemplate;
	
	@Autowired
	private Utilities utilities;	
	
	@Secured("ROLE_USER")
	@RequestMapping(method=RequestMethod.POST)
	public void save(@RequestBody Map<String,String> expense) {
		//check if it has the "key" key
		if (!expense.containsKey(keyLabel)) {
			//need to add it
			int keyCount = utilities.getKeyValue(expenseKey, redisTemplate);
			//create the key
			expense.put(keyLabel, expenseKey + keyCount);
		}//end if
		//persist
		redisTemplate.opsForHash().putAll(expense.get(keyLabel), expense);
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public List<Map<?,?>> findAll() {
		//return
		return utilities.findAll(expenseKey, keyLabel, redisTemplate);
	}

	@RequestMapping(value="/total",method=RequestMethod.GET)
	public Map<String,Double> getTotalExpenses() {
		Map<String,Double> results = new HashMap<String,Double>();
		double aed = 0.00;
		double usd = 0.00;
		Set<String> keys = redisTemplate.keys(expenseKey + wildcard);
		for (String key : keys) {
			Object amount = redisTemplate.opsForHash().get(key, amountKey);
			if (amount != null) {
				Object currency = redisTemplate.opsForHash().get(key, currencyKey);
				if (currency.toString().equalsIgnoreCase("aed")) {
					aed += Double.parseDouble(amount.toString()); 
				} else {
					usd += Double.parseDouble(amount.toString());
				}//end if
			}//end if
		}//end for
		//populate
		results.put("AED", aed);
		results.put("USD",usd);
		//return
		return results;
	}
	
	@Secured("ROLE_USER")
	@RequestMapping(value="/{key}",method=RequestMethod.DELETE)
	public void delete(@PathVariable("key") String key) {
		redisTemplate.delete(key);
	}
}
