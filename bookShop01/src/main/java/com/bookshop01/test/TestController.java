package com.bookshop01.test;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {

	@Autowired
	private ApiService apiService;
	
	@RequestMapping(value= "/test/keyin.do")
	public String keyin(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		String result = "";
		
		result = "HIMEDIA";
		
		String id = "himedia"; //발급된 계정
		String base = "https://api.testpayup.co.kr";
		String path = "/v2/api/payment/"+id+"/keyin2";
		
		String url = base+path;
		//url = https://api.testpayup.co.kr/v2/api/payment/himedia/keyin2
		
		//파라미터로 사용할 맵
		Map<String,String> map = new HashMap<String,String>();
		String signature = "";
		map.put("orderNumber","TEST_12346");
		map.put("cardNo","0001000200030004");
		map.put("expireMonth","05");
		map.put("expireYear","25");
		map.put("birthday","900904");
		map.put("cardPw","09");
		map.put("amount","1000");
		map.put("quota","0");
		map.put("itemName","테스트 아이템");
		map.put("userName","테스터");
		map.put("timestamp","20221010000000");
		
		signature = apiService.encrypt(id+"|"+map.get("orderNumber")+"|"+map.get("amount")+"|ac805b30517f4fd08e3e80490e559f8e|"+map.get("timestamp"));
		
		map.put("signature",signature);
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap = apiService.restApi(map, url);
		
		System.out.println(resultMap.toString());
		
		System.out.println("응답코드 = " + resultMap.get("responseCode") );
		System.out.println("거래번호 = " + resultMap.get("transactionId") );
		
		return "";
		
	}
	
	@RequestMapping(value= "/test/cancel.do")
	public String cancel(@RequestParam Map<String, String> dateMap) throws Exception{
		
//		http://localhost:8080/bookshop01/test/cancel.do?transactionId=20221012093820ST0533
		
		//화면에서 거래번호를 받아서 취소요청
		//파라미터 안에 거래번호가 있음. transactionId
		
		System.out.println(dateMap.toString());
		System.out.println("화면에서 보낸 거래번호 = " +dateMap.get("transactionId"));
		
		String url = "https://api.testpayup.co.kr/v2/api/payment/himedia/cancel2"; //규격서상 취소URL 입력
		
		//취소 API 요청하기
		//파라미터로 사용할 맵
		Map<String,String> map = new HashMap<String,String>();
//		merchantId 가맹점아이디 가맹점 아이디
//		transactionId 거래번호 결제승인시 페이업에서 전송한 거래번호
//		signature 서명값
		String signature = "";
		map.put("merchantId", "himedia");
		map.put("transactionId", dateMap.get("transactionId"));
//		({merchantId}|{ transactionId }|{apiCertKey}
		signature = apiService.encrypt("himedia|"+dateMap.get("transactionId")+"|"+"ac805b30517f4fd08e3e80490e559f8e");
		map.put("signature", signature);
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap = apiService.restApi(map, url);
		
//		responseCode 응답코드 3.1 응답코드 테이블 참조
//		responseMsg 응답메세지 3.1 응답코드 테이블 참조
//		cancelDateTime 
		
		System.out.println(resultMap.toString());
		
		System.out.println("응답값 (응답코드) = " + resultMap.get("responseCode"));
		System.out.println("응답값 (응답메세지) = " + resultMap.get("responseMsg"));
		System.out.println("응답값 (취소시간) = " + resultMap.get("cancelDateTime"));
		
		
		return "";
		
	}
	
	/*
	 * 인증결제 주문데이터 받기
	 * 인증결제 규격서 중 (3.1 주문요청 하기)
	 */
	@RequestMapping(value= "/test/order.do")
	public String order(@RequestParam Map<String, String> dateMap) throws Exception{
		
		String id = "himedia";
		
		String orderNumber = "";
		String amount = "";
		String itemName = "";
		String userName = "";
		String userAgent = "WP";
		String returnUrl = "";
		String timestamp = "";
		String signature = "";
		//파라미터로 사용할 맵
		Map<String,String> map = new HashMap<String,String>();

		map.put("orderNumber",orderNumber);
		map.put("amount",amount);
		map.put("itemName",itemName);
		map.put("userName",userName);
		map.put("userAgent",userAgent);
		map.put("returnUrl",returnUrl);
		map.put("timestamp",timestamp);
		
//		signature {merchantId}|{orderNumber}|{amount}|{apiCertKey}|{timestamp}
		signature = apiService.encrypt(id+"|"+map.get("orderNumber")+"|"+map.get("amount")+"|ac805b30517f4fd08e3e80490e559f8e|"+map.get("timestamp"));
		map.put("signature",signature);
		
		//아래 부분 완성 하기
		
		return "";
	}

	
	
	@RequestMapping(value= "/test/kakaoOrder.do")
	public Map<String,Object> kakaoOrder(@RequestParam Map<String, String> dateMap) throws Exception{
		
//		{orderNumber=order20221014, amount=30000, itemName=짠테크 가계부, userName=이병승}
		System.out.println(dateMap.toString());
		
		String id = "himedia"; //발급된 계정
		String base = "https://api.testpayup.co.kr";
		String path = "/ep/api/kakao/"+id+"/order";
		
		String url = base+path;
		
		String signature = ""; 
		
		//파라미터로 사용할 맵
		Map<String,String> map = new HashMap<String,String>();
		map.put("orderNumber","TEST_12346"); //주문데이터 생성해야지 생기는 키값 (생성을 안해서 임의값)
		map.put("amount",dateMap.get("100")); //화면에서 받아온 값 (테스트를 위해서 100원으로 고정)
		map.put("itemName",dateMap.get("itemName")); //화면에서 받아온 값
		map.put("userName",dateMap.get("userName")); //화면에서 받아온 값
		map.put("returnUrl","naver.com");//PC에서는 안중요함
		map.put("timestamp","1000");
		map.put("userAgent","WP");
		
		signature = apiService.encrypt(id+"|"+map.get("orderNumber")+"|"+map.get("amount")+"|ac805b30517f4fd08e3e80490e559f8e|"+map.get("timestamp"));
		
		map.put("signature",signature);
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap = apiService.restApi(map, url);
		
		System.out.println(resultMap.toString());
		
		System.out.println("응답코드 = " + resultMap.get("responseCode") );
		
		return resultMap;
	}
}
