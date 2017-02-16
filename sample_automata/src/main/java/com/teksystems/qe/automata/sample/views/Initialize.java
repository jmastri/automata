package com.teksystems.qe.automata.sample.views;


import org.openqa.selenium.WebDriver;

import com.teksystems.qe.automata.annotations.ViewStates;
import com.teksystems.qe.automata.sample.data.GenericDataObject;

@ViewStates("initialize")
public class Initialize extends GoogleBaseView {

	public Initialize(WebDriver driver, GenericDataObject data) {
		super(driver, data);
	}

	public void complete() {
		driver.navigate().to("http://google.com");
	}

}
