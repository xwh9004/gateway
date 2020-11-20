package com.example.gateway.router;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * <p><b>Description:</b>
 * TODO
 * <p><b>Company:</b>
 *
 * @author created by Jesse Hsu at 9:57 on 2020/11/3
 * @version V0.1
 * @classNmae RandomHttpEndpointRouter
 */
@Component
public class RandomHttpEndpointRouter implements HttpEndpointRouter{

    public static Map<String,List<String>> routerMap = new HashMap();

    @Value("${gateway.server.context-path}")
    private  String contextPath;

    public static final String ROOT_PATH = "/";
    static {



        List<String> userServices = new ArrayList<>();

        userServices.add("http://localhost:8801");
//        userServices.add("http://localhost:8802/");
//        userServices.add("http://localhost:8803/");

        routerMap.put("userService",userServices);
    }

    @Override
    public String route(List<String> endpoints) {
        return null;
    }

    /**
     * 根据代理请求获取后台地址
     *
     * @param endpoints@return
     */
    @Override
    public String route(String endpoints) {

        String serviceName = getServiceName(endpoints);
        String serviceUrlPath = endpoints.substring(contextPath.length()+serviceName.length());
        List<String> backendRouters =routerMap.get(serviceName);
        Random rand =new Random();
        return backendRouters.get(rand.nextInt(backendRouters.size())).concat(serviceUrlPath);
    }

    private String getServiceName(String endpoints) {

        if(endpoints.startsWith(contextPath)){
            String serviceUrl = endpoints.substring(contextPath.length());
            if(StringUtils.hasText(serviceUrl)){
                int index = serviceUrl.indexOf("/");
                return serviceUrl.substring(0,index);
            }
        }
        if(endpoints.equals(contextPath)||endpoints.equals(contextPath.substring(0,contextPath.length()-1))){
            return ROOT_PATH;
        }
        return null;
    }
}
