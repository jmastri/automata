package com.teksystems.qe.automata.sample.views;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.teksystems.qe.automata.annotations.ViewStates;
import com.teksystems.qe.automata.sample.app.GoogleBaseView;
import com.teksystems.qe.automata.sample.data.GenericDataObject;

@ViewStates("search")
public class HomePage extends GoogleBaseView {

	@FindBy(id="lst-ib")
	WebElement searchInput;
	@FindBy(xpath="//button[@aria-label='Google Search']")
	WebElement startSearch;
	
	public HomePage(WebDriver driver, GenericDataObject data) {
		super(driver, data);
	}

	public void complete() {
		searchInput.sendKeys(data.getQuery());
		startSearch.click();
	}

}
