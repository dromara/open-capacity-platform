package com.open.capacity.uaa.json;

public  class JacksonMapper implements  JsonMapper {
	private com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

	@Override
	public String write(Object input) throws Exception {
		return mapper.writeValueAsString(input);
	}

	@Override
	public <T> T read(String input, Class<T> type) throws Exception {
		return mapper.readValue(input, type);
	}
}
