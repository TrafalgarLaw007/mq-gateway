package xin.keepmoving.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author
 * @date 2020-08-14 10:18
 */
@WebService(name = "WebServiceCase", portName = "WaterServiceTemplatePort", targetNamespace = "http://service.keepmoving.xin/")
public interface IWebService {
	/**
	 *
	 * @param valuesXml
	 * @return
	 */
	@WebMethod(operationName = "sendValues")
	String sendValues(String valuesXml);
}
