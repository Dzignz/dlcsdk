package com.mogan.model;

import java.util.Map;

import com.mogan.exception.netAgent.AccountNotExistException;
import com.mogan.model.netAgent.NetAgentYJ;

import com.mogan.sys.model.ProtoModel;
import com.mogan.sys.model.ProxyModelFace;

public class ItemOrderFormYJ extends ProtoModel implements ProxyModelFace {

	/**
	 * GET_ORDER_FORM
	 * @param bidId
	 * @param itemId
	 * @return
	 * @throws AccountNotExistException 
	 */
	private String getOrderForm(String bidAccount,String itemId,String sellerAccount) throws AccountNotExistException{
		NetAgentYJ agentYJ = new NetAgentYJ(this.getModelServletContext(),
				this.getAppId());
//		jArray = agentYJ.getItemOrderForm(bidId, itemId).get(0);
		return agentYJ.getItemOrderForm(bidAccount, itemId,sellerAccount).getString(0);
	}
	
	/**
	 * 
	 * @param postMap
	 * @return
	 * @throws AccountNotExistException 
	 */
	private String getOrderFormPreview(Map postMap) throws AccountNotExistException{
		NetAgentYJ agentYJ = new NetAgentYJ(this.getModelServletContext(),
				this.getAppId());
		String bidAccount=(String) postMap.get("UID");
		String itemId=(String) postMap.get("ITEM_ID");
//		agentYJ.getOrderFormPreview(bidAccount, postMap);
		return agentYJ.getOrderFormPreview(bidAccount,itemId, postMap).getString(0);
	}
	
	/**
	 * 
	 * @param postMap
	 * @return
	 * @throws AccountNotExistException 
	 */
	private String postOrderForm(Map postMap) throws AccountNotExistException{
		NetAgentYJ agentYJ = new NetAgentYJ(this.getModelServletContext(),
				this.getAppId());
		String bidAccount=(String) postMap.get("UID");
//		agentYJ.getOrderFormPreview(bidAccount, postMap);
		return agentYJ.postOrderForm(bidAccount, postMap).getString(0);
	}
	
	@Override
	public String doAction(Map parameterMap) throws Exception {
		// TODO Auto-generated method stub
		String htmlStr=new String();
		if (this.getAct().equals("GET_ORDER_FORM")){
			String bidId=(String) parameterMap.get("BID_ACCOUNT");
			String itemId=(String) parameterMap.get("ITEM_ID");
			String sellerAccount=(String) parameterMap.get("SELLER_ACCOUNT");	
			htmlStr=getOrderForm(bidId,itemId,sellerAccount);
		}else if (this.getAct().equals("GET_ORDER_FORM_PREVIEW")){
			htmlStr=getOrderFormPreview(parameterMap);
		}else if (this.getAct().equals("POST_ORDER_FORM")){
			htmlStr=postOrderForm(parameterMap);
		}
		return htmlStr;
	}

}
