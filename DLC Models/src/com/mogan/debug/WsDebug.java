package com.mogan.debug;

import java.util.Iterator;
import java.util.Map;
import net.sf.json.JSONArray;
import com.mogan.sys.model.ProtoModel;
import com.mogan.sys.model.ServiceModelFace;

public class WsDebug  extends ProtoModel implements ServiceModelFace{

	@Override
	public JSONArray doAction(Map parameterMap) throws Exception {
		// TODO Auto-generated method stub
		if (this.getAct().equals("SHOW_PARAS_DATA")){
			Iterator it = parameterMap.keySet().iterator();
			for (;it.hasNext();){
				String key=(String) it.next();
			}
		}
		return null;
	}

}
