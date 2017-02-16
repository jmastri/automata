package com.teksystems.qe.automata.sample.amazon.app;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.teksystems.qe.automata.Application;

import com.teksystems.qe.automata.sample.amazon.data.AmazonUser;

public class AmazonApplication extends Application {
    private WebDriver driver;
    private AmazonUser user;
    public AmazonApplication(WebDriver driver, AmazonUser user){
        
        this.driver = driver;
        this.user   = user;        
        config.setBasePackage("com.teksystems.qe.automata.sample.amazon.views");
    }
    
    @Override
    protected String getState() {
        
        try {
            String sUrl                 = driver.getCurrentUrl();
            URL url                     = new URL(sUrl);
            Map<String, String> query   = getQueryMap(url.getQuery());

            if(url.getPath().equalsIgnoreCase("/")){
                return "home";
            }

            if(query.containsKey("url")){
                return query.get("url");
            }
            
            if(query.containsKey("qid")){
                return "detail";
            }
            
        } catch (MalformedURLException e) {
            LOG.error(e);
        }
        return null;
    }

    @Override
    protected Object[] getViewData() {
        Object[] data = new Object[]{driver,user};
        return data;
    }
    
    private Map<String, String> getQueryMap(String query)  
    {  
        Map<String, String> map = new HashMap<String, String>(); 
        if(query == null || query.isEmpty()){
            return map;
        }
        String[] params = query.split("&");  
         
        for (String param : params)  
        {  
            String name = param.split("=")[0];  
            String value = param.split("=")[1];  
            map.put(name, value);  
        }  
        return map;  
    }

}
