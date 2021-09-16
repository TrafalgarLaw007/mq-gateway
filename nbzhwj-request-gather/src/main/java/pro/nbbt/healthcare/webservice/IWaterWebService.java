package pro.nbbt.healthcare.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author linyibeng 
 * @date 2020-08-14 10:18
 */
@WebService(name = "WaterService", portName = "WaterServiceTemplatePort", targetNamespace = "http://service.server.huntto.com/")
public interface IWaterWebService {
	/**
	 * 水质监测实时数据
	 * @param valuesXml
	 * @return
	 */
	@WebMethod(operationName = "sendValues")
	String sendValues(String valuesXml);
}
