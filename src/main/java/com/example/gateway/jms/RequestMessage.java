package com.example.gateway.jms;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * <p><b>Description:</b>
 * TODO
 * <p><b>Company:</b>
 *
 * @author created by Jesse Hsu at 13:04 on 2020/11/26
 * @version V0.1
 * @classNmae RequestMessage
 */
@NoArgsConstructor
@Data
public class RequestMessage implements Serializable {

    private String version;

    private String serviceName;

    private String url;

    private int port;

    private String host;

    private String method;

    private Map<String,Object> requsetParams;
}
