package com.togic.weixin.login.controller;

import com.togic.weixin.login.utils.AESUtil;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import weixin.popular.api.SnsAPI;
import weixin.popular.bean.sns.SnsToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@Controller
@RequestMapping("/weixin")
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${weixin.appid}")
    private String appid;
    @Value("${weixin.secret}")
    private String secret;
    @Value("${weixin.login}")
    private String weixinLogin;

    private String weixinUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=abc&connect_redirect=1#wechat_redirect";

    @RequestMapping("/login.do")
    public void login(HttpServletRequest request,HttpServletResponse response, String redirect) throws IOException {
        redirect = new String(Base64Utils.decodeFromUrlSafeString(redirect),"utf-8");
        request.getSession().setAttribute("weixin_redirect",redirect);
        response.sendRedirect(String.format(weixinUrl,appid, URLEncoder.encode(weixinLogin,"utf-8")));
    }


    @RequestMapping("/redirect.do")
    public void redirect(HttpServletRequest request, HttpServletResponse response, @RequestParam String code) throws IOException {
        SnsToken snsToken = SnsAPI.oauth2AccessToken(appid,secret,code);
        logger.info("login>>snsToken:" + snsToken.getAccess_token());
        logger.info("login>>openid:" + snsToken.getOpenid());
        String redirect = (String) request.getSession().getAttribute("weixin_redirect");
        logger.info("login>>redirect:" + redirect);
        StringBuilder sb = new StringBuilder(redirect);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("openid",snsToken.getOpenid());
        jsonObject.put("accessToken",snsToken.getAccess_token());
        String weixinData = AESUtil.encrypt(jsonObject.toString());

        if(redirect.contains("?")) {
            sb.append("&weixinData=");
            sb.append(weixinData);
        } else {
            sb.append("?weixinData=");
            sb.append(weixinData);
        }
        logger.info("login>>redirect:" + jsonObject.toString());
        response.sendRedirect(sb.toString());
    }


    @RequestMapping("/test.do")
    public void test(HttpServletRequest request,HttpServletResponse response) throws IOException {
        response.sendRedirect("http://www.baidu.com");
    }
}
