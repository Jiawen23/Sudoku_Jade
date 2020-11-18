package model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Model {
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public String serialize() {
		try {
			return getMapper().writeValueAsString(this);
		} catch(JsonProcessingException ex) { 
			System.err.println("Unable to serialize" + this.getClass().getCanonicalName() + " object");
			ex.printStackTrace();
			return null;
		}
	}
	

	public static <T extends Model> T deserialize(String serial, Class<T> c) {
		try {
			return getMapper().readValue(serial, c);
		} catch (IOException e) { 
			System.err.println("Unable to deserialize" + c.getCanonicalName() + " object");
			e.printStackTrace();
			return null;
		}
	}
	
	private static ObjectMapper getMapper() {
		return mapper;
	}
}