package com.example.gateway.router;

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
public class RandomHttpEndpointRouter implements HttpEndpointRouter{

    public static Map<String,List<String>> routerMap = new HashMap();

    static {

        List<String> rootServiceRouters = new ArrayList<>();
        rootServiceRouters.add("/");
        routerMap.put("/",rootServiceRouters);
        List<String> helloServiceRouters = new ArrayList<>();

        helloServiceRouters.add("http://localhost:8801/");
        helloServiceRouters.add("http://localhost:8802/");
        helloServiceRouters.add("http://localhost:8803/");

        routerMap.put("/hello",helloServiceRouters);
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
        List<String> backendRouters =routerMap.get(serviceName);
        Random rand =new Random();
        return backendRouters.get(rand.nextInt(backendRouters.size()));
    }

    private String getServiceName(String endpoints) {

        if(endpoints.startsWith("/api")){
            String serviceName = endpoints.substring(4);
            return serviceName;
        }
        return null;
    }
}
