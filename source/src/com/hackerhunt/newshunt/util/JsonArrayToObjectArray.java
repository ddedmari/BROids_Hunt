package com.hackerhunt.newshunt.util;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JsonArrayToObjectArray {

	static class NavItem {

		private String category;
		private String name;

		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public NavItem() {
		}
	}

	public void convert(String jsonString) throws JsonParseException, JsonMappingException,
			IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		
	    List<NavItem> navigation;
	    
		navigation = objectMapper.readValue(jsonString,
				objectMapper.getTypeFactory().constructCollectionType(
						List.class, NavItem.class));
		
		
		for (NavItem navItem : navigation) {
			System.out.println("Name "+navItem.name);
			System.out.println("Category "+navItem.category);
			
		}
	}

}