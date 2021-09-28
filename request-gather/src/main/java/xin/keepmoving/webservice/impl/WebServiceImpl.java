package xin.keepmoving.webservice.impl;

import lombok.extern.slf4j.Slf4j;
import xin.keepmoving.config.WebServiceConfig;
import xin.keepmoving.utils.SpringContextHolder;
import xin.keepmoving.webservice.IWebService;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

/**
 * @author
 * @date
 */
@Slf4j
@WebService(name = "WaterService")
public class WebServiceImpl extends BaseWebService implements IWebService {

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
		Endpoint.publish(url, new WebServiceImpl());
		log.info("{} WebService服务发布成功。", url);
	}
}
