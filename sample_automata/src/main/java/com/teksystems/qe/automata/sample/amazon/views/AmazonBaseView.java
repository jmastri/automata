package com.teksystems.qe.automata.sample.amazon.views;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import com.teksystems.qe.automata.interfaces.BaseView;
import com.teksystems.qe.automata.sample.amazon.data.AmazonUser;


public abstract class AmazonBaseView implements BaseView{

	protected WebDriver driver;
	protected AmazonUser user;
	/**
	 * Define global elements here
	 */
    @FindBy(xpath="//input[@name='field-keywords']")
    WebElement searchInput;
    @FindBy(xpath="//input[contains(@class,'nav-input') and @type='submit']")
    WebElement searchSubmit;
	
	public AmazonBaseView(WebDriver driver, AmazonUser user){
		this.driver = driver;
		this.user	= user;
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
