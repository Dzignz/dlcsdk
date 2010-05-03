package com.mogan.model;

import java.util.Map;
import net.sf.json.JSONArray;
import com.mogan.encode.zhcode;
import com.mogan.sys.model.ProtoModel;
import com.mogan.sys.model.ServiceModelFace;

/**
 * modelname=TransCode<br>
 *  <li> GB2312	= 0</li>
    <li> GBK           = 1</li>
    <li> GB18030       = 2</li>
    <li> HZ            = 3</li>
    <li> BIG5          = 4</li>
    <li> CNS11643      = 5</li>
    <li> UTF8          = 6</li>
    <li> UTF8T         = 7</li>
    <li> UTF8S         = 8</li>
    <li> UNICODE       = 9</li>
    <li> UNICODET      = 10</li>
    <li> UNICODES      = 11</li>
    <li> ISO2022CN     = 12</li>
    <li> ISO2022CN_CNS = 13</li>
    <li> ISO2022CN_GB  = 14</li>
    <li> EUC_KR        = 15</li>
    <li> CP949         = 16</li>
    <li> ISO2022KR     = 17</li>
    <li> JOHAB         = 18</li>
    <li> SJIS          = 19</li>
    <li> EUC_JP        = 20</li>
    <li> ISO2022JP     = 21</li>
 * @author user
 *
 */
public class TransCode extends ProtoModel implements ServiceModelFace {

	/**
	 * <P>
	 * <font size=7 color=red>轉換字碼，ACTION =TRANS_CODE</font>
	 * </P>
	 * EXP:繁轉簡
	 * <li>VALUE=繁體中文</li>
	 * <li>FROM_CODE=4</li>
	 * <li>TO_CODE=0</li>
	 * 
	 * @param value - 字串
	 * @param fromCode - 原本編碼
	 * @param toCode - 目標編碼
	 * @return
	 */
	public JSONArray trans(String value,int fromCode,int toCode){
		JSONArray jArray=new JSONArray();
		zhcode mycode = new zhcode();
		jArray.add(mycode.convertString(value,fromCode,toCode));
		return jArray;
	}
	public static void main(String [] args){
		zhcode mycode = new zhcode();
		System.out.println(mycode.convertString("等軟體製造商推出自己品牌的",4,0));
		
	}
	
	@Override
	public JSONArray doAction(Map parameterMap) throws Exception {
		// TODO Auto-generated method stub
		JSONArray jArray=new JSONArray();
		if (this.getAct().equals("TRANS_CODE")) {
			String value=(String) parameterMap.get("VALUE");
			int fromCode=Integer.parseInt((String) parameterMap.get("FROM_CODE"));
			int toCode=Integer.parseInt((String) parameterMap.get("TO_CODE"));
			jArray=trans(value,fromCode,toCode);
		}
		return jArray;
	}

}
