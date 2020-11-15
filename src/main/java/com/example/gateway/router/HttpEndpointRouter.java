package com.example.gateway.router;

import java.util.List;

public interface HttpEndpointRouter {
    
    String route(List<String> endpoints);

    /**
     * 根据代理请求获取后台地址
     * @param
     * @return
     */
    String route(String endpoints);
    
}
