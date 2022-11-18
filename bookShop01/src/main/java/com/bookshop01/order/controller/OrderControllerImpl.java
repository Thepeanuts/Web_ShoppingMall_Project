package com.bookshop01.order.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.bookshop01.common.base.BaseController;
import com.bookshop01.goods.vo.GoodsVO;
import com.bookshop01.member.vo.MemberVO;
import com.bookshop01.order.service.OrderService;
import com.bookshop01.order.vo.OrderVO;
import com.bookshop01.test.ApiService;

@Controller("orderController")
@RequestMapping(value="/order")
public class OrderControllerImpl extends BaseController implements OrderController {
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderVO orderVO;
	@Autowired
	private ApiService apiService;
	
	@RequestMapping(value="/orderEachGoods.do" ,method = RequestMethod.POST)
	public ModelAndView orderEachGoods(@ModelAttribute("orderVO") OrderVO _orderVO,
			                       HttpServletRequest request, HttpServletResponse response)  throws Exception{
		
		request.setCharacterEncoding("utf-8");
		HttpSession session=request.getSession();
		session=request.getSession();
		
		Boolean isLogOn=(Boolean)session.getAttribute("isLogOn");
		String action=(String)session.getAttribute("action");
		//로그인 여부 체크
		//이전에 로그인 상태인 경우는 주문과정 진행
		//로그아웃 상태인 경우 로그인 화면으로 이동
		if(isLogOn==null || isLogOn==false){
			session.setAttribute("orderInfo", _orderVO);
			session.setAttribute("action", "/order/orderEachGoods.do");
			return new ModelAndView("redirect:/member/loginForm.do");
		}else{
			 if(action!=null && action.equals("/order/orderEachGoods.do")){
				orderVO=(OrderVO)session.getAttribute("orderInfo");
				session.removeAttribute("action");
			 }else {
				 orderVO=_orderVO;
			 }
		 }
		
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		
		List myOrderList=new ArrayList<OrderVO>();
		myOrderList.add(orderVO);

		MemberVO memberInfo=(MemberVO)session.getAttribute("memberInfo");
		
		session.setAttribute("myOrderList", myOrderList);
		session.setAttribute("orderer", memberInfo);
		return mav;
	}
	
	@RequestMapping(value="/orderAllCartGoods.do" ,method = RequestMethod.POST)
	public ModelAndView orderAllCartGoods( @RequestParam("cart_goods_qty")  String[] cart_goods_qty,
			                 HttpServletRequest request, HttpServletResponse response)  throws Exception{
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		HttpSession session=request.getSession();
		Map cartMap=(Map)session.getAttribute("cartMap");
		List myOrderList=new ArrayList<OrderVO>();
		
		List<GoodsVO> myGoodsList=(List<GoodsVO>)cartMap.get("myGoodsList");
		MemberVO memberVO=(MemberVO)session.getAttribute("memberInfo");
		
		for(int i=0; i<cart_goods_qty.length;i++){
			String[] cart_goods=cart_goods_qty[i].split(":");
			for(int j = 0; j< myGoodsList.size();j++) {
				GoodsVO goodsVO = myGoodsList.get(j);
				int goods_id = goodsVO.getGoods_id();
				if(goods_id==Integer.parseInt(cart_goods[0])) {
					OrderVO _orderVO=new OrderVO();
					String goods_title=goodsVO.getGoods_title();
					int goods_sales_price=goodsVO.getGoods_sales_price();
					String goods_fileName=goodsVO.getGoods_fileName();
					_orderVO.setGoods_id(goods_id);
					_orderVO.setGoods_title(goods_title);
					_orderVO.setGoods_sales_price(goods_sales_price);
					_orderVO.setGoods_fileName(goods_fileName);
					_orderVO.setOrder_goods_qty(Integer.parseInt(cart_goods[1]));
					myOrderList.add(_orderVO);
					break;
				}
			}
		}
		session.setAttribute("myOrderList", myOrderList);
		session.setAttribute("orderer", memberVO);
		return mav;
	}	
	
	@RequestMapping(value="/payToOrderGoods.do" ,method = RequestMethod.POST)
	public ModelAndView payToOrderGoods(@RequestParam Map<String, String> receiverMap,
			                       HttpServletRequest request, HttpServletResponse response)  throws Exception{
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		
		//receiverMap.get("cardNo"); //카드번호
		//receiverMap.get("expireMonth"); //expireMonth
		//receiverMap.get("expireYear"); //expireYear
		//receiverMap.get("birthday"); //birthday
		//receiverMap.get("cardPw"); //cardPw
		
		HttpSession session=request.getSession();
		MemberVO memberVO=(MemberVO)session.getAttribute("orderer");
		String member_id=memberVO.getMember_id();
		String orderer_name=memberVO.getMember_name();
		String orderer_hp = memberVO.getHp1()+"-"+memberVO.getHp2()+"-"+memberVO.getHp3();
		List<OrderVO> myOrderList=(List<OrderVO>)session.getAttribute("myOrderList");
		
		for(int i=0; i<myOrderList.size();i++){
			OrderVO orderVO=(OrderVO)myOrderList.get(i);
			orderVO.setMember_id(member_id);
			orderVO.setOrderer_name(orderer_name);
			orderVO.setReceiver_name(receiverMap.get("receiver_name"));
			
			orderVO.setReceiver_hp1(receiverMap.get("receiver_hp1"));
			orderVO.setReceiver_hp2(receiverMap.get("receiver_hp2"));
			orderVO.setReceiver_hp3(receiverMap.get("receiver_hp3"));
			orderVO.setReceiver_tel1(receiverMap.get("receiver_tel1"));
			orderVO.setReceiver_tel2(receiverMap.get("receiver_tel2"));
			orderVO.setReceiver_tel3(receiverMap.get("receiver_tel3"));
			
			orderVO.setDelivery_address(receiverMap.get("delivery_address"));
			orderVO.setDelivery_message(receiverMap.get("delivery_message"));
			orderVO.setDelivery_method(receiverMap.get("delivery_method"));
			orderVO.setGift_wrapping(receiverMap.get("gift_wrapping"));
			orderVO.setPay_method(receiverMap.get("pay_method"));
			orderVO.setCard_com_name(receiverMap.get("card_com_name"));
			orderVO.setCard_pay_month(receiverMap.get("card_pay_month"));
			orderVO.setPay_orderer_hp_num(receiverMap.get("pay_orderer_hp_num"));	
			orderVO.setOrderer_hp(orderer_hp);
			myOrderList.set(i, orderVO); // 각 orderVO에 주문자 정보를 세팅한 후 다시 myOrderList에 저장한다.
		} // end for
		
	    orderService.addNewOrder(myOrderList); //이게 주문 데이터 생성하는 서비스 
	    //*T_SHOPPING_ORDER 테이블에 데이터가 들어감
	    
	    //신용카드 결제 요청(수기인증방식)
	    //주문데이터를 생성하고 결제하기. 결제소스 붙여넣는중 
	    String orderNumber = "";
	    String amount = "";
	    String itemName = "";
	    String userName = "";
	    
	    for(OrderVO vo : myOrderList) {
	    	orderNumber = String.valueOf(vo.getOrder_seq_num()); 
	    	amount = String.valueOf(vo.getGoods_sales_price());
	    	itemName = vo.getGoods_title();
	    	userName = vo.getOrderer_name();
	    }
	    
	    String id = "himedia"; //발급된 계정
		String base = "https://api.testpayup.co.kr";
		String path = "/v2/api/payment/"+id+"/keyin2";
		
		String url = base+path;
		//url = https://api.testpayup.co.kr/v2/api/payment/himedia/keyin2
		
		//파라미터로 사용할 맵
		Map<String,String> map = new HashMap<String,String>();
		String signature = "";
		map.put("orderNumber",orderNumber);
		map.put("cardNo",receiverMap.get("cardNo"));
		map.put("expireMonth",receiverMap.get("expireMonth"));
		map.put("expireYear",receiverMap.get("expireYear"));
		map.put("birthday",receiverMap.get("birthday"));
		map.put("cardPw",receiverMap.get("cardPw"));
		map.put("amount",amount);
		map.put("quota","0");
		map.put("itemName",itemName);
		map.put("userName",userName);
		map.put("timestamp","20221010000000");
		
		signature = apiService.encrypt(id+"|"+map.get("orderNumber")+"|"+map.get("amount")+"|ac805b30517f4fd08e3e80490e559f8e|"+map.get("timestamp"));
		
		map.put("signature",signature);
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap = apiService.restApi(map, url);
		
		//미완성
		System.out.println("응답코드 = " + resultMap.get("responseCode") );
		System.out.println("거래번호 = " + resultMap.get("transactionId") );
	    
		String responseCode = (String) resultMap.get("responseCode");		
		
	    //결제 성공/실패 여부에 따라서 화면 바꾸기, 데이터 업데이트
	    
//		if("0000" == responseCode) (X)
	    if("0000".equals(responseCode)) {
	    	//여기는 결제가 성공했을 때
	    	
	    	//DB데이터 업데이트 (스킵)
	    	
	    	mav.setViewName("/order/payToOrderGoods");
	    	
	    	//결제정보 보내기
	    	mav.addObject("responseCode", resultMap.get("responseCode"));
	    	mav.addObject("responseMsg", resultMap.get("responseMsg"));
	    	
	    	mav.addObject("cardName", resultMap.get("cardName")); //카드사명
	    	mav.addObject("authNumber", resultMap.get("authNumber")); //카드승인번호
	    	mav.addObject("authDateTime", resultMap.get("authDateTime")); //카드승인일시
	    	
	    }else {
	    	//여기는 결제가 실패했을 때
	    	
	    	//DB데이터 업데이트 (스킵)
	    	
	    	//JSP변경
	    	mav.setViewName("/order/orderResultFail");
	    	
	    	mav.addObject("responseCode", resultMap.get("responseCode"));
	    	mav.addObject("responseMsg", resultMap.get("responseMsg"));
	    	
	    }
	    
		mav.addObject("myOrderInfo",receiverMap);
		mav.addObject("myOrderList", myOrderList);
		
		return mav;
	}

}
