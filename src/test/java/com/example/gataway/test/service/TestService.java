package com.example.gataway.test.service;

import org.springframework.stereotype.Service;

/**
 * <p><b>Description:</b>
 * TODO
 * <p><b>Company:</b>
 *
 * @author created by Jesse Hsu at 13:13 on 2020/11/20
 * @version V0.1
 * @classNmae TestService
 */
@Service
public class TestService {

    public void test1(){
        System.out.println("this is test1");
    }

    public void test2(){
        test1();
        System.out.println("this is test2");
    }
}
