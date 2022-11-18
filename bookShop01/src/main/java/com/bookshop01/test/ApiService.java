package com.bookshop01.test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class ApiService {
	
	public Map<String, Object> restApi(Map<String, String> map, String url) throws Exception {
		
		String param = "";
		
		ObjectMapper mapper = new ObjectMapper();
		param = mapper.writeValueAsString(map);
		
		// **고정 값(아래)
		
		// 아래부터는 OkHttp 사용
		// OkHttp = REST API, HTTP 통신을 간편하게 사용할 수 있도록 만들어진 라이브러리
		OkHttpClient client = new OkHttpClient();
		
		MediaType mediaType = MediaType.parse("application/json");	// application/json **중요
		RequestBody Body = RequestBody.create(mediaType, param);
		Request request = new Request.Builder().url(url).post(Body).addHeader("cache-control", "no-cache").build();
		
		// 결과 값 받기
		Response response = client.newCall(request).execute();
		String result = response.body().string();
		
		ObjectMapper resultMapper = new ObjectMapper();
		Map<String, Object> resultMap = resultMapper.readValue(result, Map.class);
		
		return resultMap;
		
	}
	
	// SHA256 암호화
		public String encrypt(String text) throws NoSuchAlgorithmException {
	        MessageDigest md = MessageDigest.getInstance("SHA-256");
	        md.update(text.getBytes());

	        return bytesToHex(md.digest());
	    }

	    private String bytesToHex(byte[] bytes) {
	        StringBuilder builder = new StringBuilder();
	        for (byte b : bytes) {
	            builder.append(String.format("%02x", b));
	        }
	        return builder.toString();
	    }

}