package com.teksystems.qe.automata.sample.views;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.teksystems.qe.automata.interfaces.BaseView;
import com.teksystems.qe.automata.sample.data.GenericDataObject;

public abstract class GoogleBaseView implements BaseView{

	protected WebDriver driver;
	protected GenericDataObject data;
	/**
	 * Define global elements here
	 */
	
	public GoogleBaseView(WebDriver driver, GenericDataObject data){
		this.driver = driver;
		this.data	= data;
		PageFactory.initElements(driver, this);
	}
	
	public abstract void complete();

	
	public void navigate() {
		return;
	}
	
	/**
	 * Put utility functions here
	 */

}
