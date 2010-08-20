package com.mogan.serviceProtal;

import javax.servlet.ServletContext;

import com.mogan.sys.SysKernel;
import com.mogan.sys.model.ModelManager;
import com.mogan.sys.model.ProtoModel;

public class BeanProtal {
	static ServletContext servletContext;
	{
		//servletContext=SysKernel.getSysContext();
	}
	static Object getBean(String beanName){
		ModelManager modelManager = new ModelManager();
		ProtoModel serviceModel = modelManager.getServiceModel(beanName);
		return serviceModel;
	}
}
