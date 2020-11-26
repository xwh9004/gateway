package com.example.gateway.jms;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
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
public class ResponseMessage implements Serializable {

    private Map<String,String> heads = new HashMap<>();


}
