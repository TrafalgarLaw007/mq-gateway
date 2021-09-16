package pro.nbbt.healthcare.webservice.impl;

import lombok.extern.slf4j.Slf4j;
import pro.nbbt.healthcare.config.WebServiceConfig;
import pro.nbbt.healthcare.utils.SpringContextHolder;
import pro.nbbt.healthcare.webservice.IWaterWebService;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

/**
 * @author linyibeng
 * @date 2020-08-14 10:21
 */
@Slf4j
@WebService(name = "WaterService")
public class WaterWebServiceImpl extends BaseWebService implements IWaterWebService {

	private static String publishUrl;

	@Override
	public String sendValues(@WebParam(name = "xml") String valuesXml) {
		log.debug("接收到:{}", valuesXml);
		return webServiceSender(publishUrl, valuesXml);
	}

	public static void publishWebService() {
		WebServiceConfig webServiceConfig = SpringContextHolder.getBean(WebServiceConfig.class);
		String ip = webServiceConfig.getPublishIp();
		String url = String.format(webServiceConfig.getUrl(), ip, webServiceConfig.getPublishPort().toString());
		publishUrl = url;
		Endpoint.publish(url, new WaterWebServiceImpl());
		log.info("{} WebService服务发布成功。", url);
	}
}
