package de.incompleteco.soaps.app.web;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.incompleteco.soaps.app.Application;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes=Application.class)
public class IngredientControllerIT {

	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	private RedisTemplate<String,?> redisTemplate;
	
	private MockMvc mockMvc;
	
	private String ingredientKey = "ingredient:";
	
	@Before
	public void before() {
		mockMvc = webAppContextSetup(context).build();
	}
	
	@After
	public void after() {
		redisTemplate.getConnectionFactory().getConnection().flushAll();//clean it all out
	}
	
	@Test
	public void test() throws Exception {
		//check before
		Set<String> keys = redisTemplate.keys(ingredientKey + "*");
		assertNotNull(keys);
		assertTrue(keys.isEmpty());
		//performce
		Map<String,String> ingredient = new HashMap<String,String>();
		ingredient.put("name","olive oil");
		//to String
		String map = new ObjectMapper().writeValueAsString(ingredient);
		//execute
		mockMvc.perform(post("/ingredient").contentType(MediaType.APPLICATION_JSON).content(map)).andExpect(status().isOk());
		//check
		keys = redisTemplate.keys(ingredientKey + "*");
		assertNotNull(keys);
		assertTrue(keys.size() > 0);
	}
	
	@Test
	public void testReturn() throws Exception {
		//execute the create
		test();
		//now retrieve
		MvcResult result = mockMvc.perform(get("/ingredient").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
		//retrieve
		String results = result.getResponse().getContentAsString();
		List<Map<String,String>> resultMap = new ObjectMapper().readValue(results, List.class);
		assertNotNull(resultMap);
		assertTrue(resultMap.size() == 1);
		System.out.println(results);
	}

}
