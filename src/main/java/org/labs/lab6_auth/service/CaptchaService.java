package org.labs.lab6_auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CaptchaService {
    @Value("${recaptcha.secret-key}")
    private String secretKey;

    @Value("${recaptcha.url}")
    private String recaptchaUrl;

    //for access to Google
    private final RestTemplate restTemplate;

    public CaptchaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean verifyCaptcha(String gRecaptchaResponse) {
        if (gRecaptchaResponse == null || gRecaptchaResponse.isEmpty()) {
            return false;
        }

        //request to Google
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("secret", secretKey);
        request.add("response", gRecaptchaResponse);

        try {
            //get response from Google
            Map<String, Object> response = restTemplate.postForObject(
                    recaptchaUrl,
                    request,
                    Map.class
            );

            return (Boolean) response.getOrDefault("success", false);

        } catch (Exception e) {
            System.err.println("Error verifying CAPTCHA: " + e.getMessage());
            return false;
        }
    }
}
