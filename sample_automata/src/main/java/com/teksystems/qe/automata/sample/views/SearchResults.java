package com.teksystems.qe.automata.sample.views;


import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.teksystems.qe.automata.annotations.ViewStates;
import com.teksystems.qe.automata.sample.app.GoogleBaseView;
import com.teksystems.qe.automata.sample.data.GenericDataObject;

@ViewStates("results")
public class SearchResults extends GoogleBaseView {

	public SearchResults(WebDriver driver, GenericDataObject data) {
		super(driver, data);
	}

	public void complete() {
		// TODO Auto-generated method stub

	}

	public WebElement findResultByUrl(String url){
		long start = System.currentTimeMillis();
		List<WebElement> results  = driver.findElements(By.tagName("cite"));
		while(results.size()==0 && System.currentTimeMillis()-start < 10*1000){
			results = driver.findElements(By.tagName("cite"));
		}
		for(WebElement each : results){
			if(each.getText()!=null && each.getText().toLowerCase().contains(url.toLowerCase()))
				return each;
		}
		
		return null;
	}
	
}