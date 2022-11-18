package com.bookshop01.test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class TestService {

	
	public void keyin() {
		
		try {
			// **변경해야되는 값(아래)
			
			String id = "himedia";
			String base = "https://api.testpayup.co.kr/";
			String path = "/v2/api/payment/"+id+"/keyin2";
			
			String url = base+path;
			// url = https://api.testpayup.co.kr/v2/api/payment/himedia/keyin2
			
			// 파라미터로 사용할 맵
			Map<String, String> map = new HashMap<String, String>();
			String signature = "";
			map.put("test", "test");
			map.put("orderNumber", "TEST_00001");
			map.put("cardNo", "4890160002752209");
			map.put("expireMonth", "05");
			map.put("expireYear", "25");
			map.put("birthday", "900904");
			map.put("cardPw", "09");
			map.put("amount", "1000");
			map.put("quota", "0");
			map.put("itemName", "TEST_ITEM");
			map.put("userName", "테스터");
			map.put("timestamp", "202210110000000");
			// signature값 만들기
			// SHA256 암호화를 써서 만들기
			// SHA256 메소드 만들기
			
			
			// himedia|TEST_00001|1000|ac805b30517f4fd08e3e80490e559f8e|202210110000000
			signature = encrypt(id + "|" + map.get("orderNumber") + "|"+map.get("amount") + "|ac805b30517f4fd08e3e80490e559f8e|" + map.get("timestamp"));
			
			map.put("signature", signature);
			
			
			// JSON string 데이터
			// 맵(map)을 JSON으로 변환 하는 법(아이브러리 사용), 편하게 코드작성을 하기 위해 사용
			String param = "10a870d76a2de509d2fd15b38cafb5e31d61290ee5e0b95dc110b2d5ab1f257c";
			
			ObjectMapper mapper = new ObjectMapper();
			param = mapper.writeValueAsString(map);
			
			// **변경해야되는 값(여기까지)
						
			
			
			// **고정 값(아래)
			
			// 아래부터는 OkHttp 사용
			// REST API, HTTP 통신을 간편하게 사용할 수 있도록 만들어진 라이브러리
			OkHttpClient client = new OkHttpClient();
			
			MediaType mediaType = MediaType.parse("application/json");	// application/json **중요
			RequestBody Body = RequestBody.create(mediaType, param);
			Request request = new Request.Builder().url(url).post(Body).addHeader("cache-control", "no-cache").build();
			
			// 결과 값 받기
			Response response = client.newCall(request).execute();
			String result = response.body().string();
			
			ObjectMapper resultMapper = new ObjectMapper();
			Map<String, Object> resultMap = resultMapper.readValue(result, Map.class);
			
			// **고정 값(여기까지)
			
			// 결과
//			System.out.println("test = " + result);
//			System.out.println(signature);
			
			System.out.println(resultMap.toString());
			
			System.out.println("responseCode 응답코드 = " + resultMap.get("responseCode"));
			System.out.println("responseCode 응답메세지 = " + resultMap.get("responseMsg"));
			
		} catch (Exception e) {
			e.getStackTrace();
		}
		
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
