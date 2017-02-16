package com.teksystems.qe.automata.sample.amazon.test;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import com.teksystems.qe.automata.Application;
import com.teksystems.qe.automata.interfaces.BaseView;
import com.teksystems.qe.automata.interfaces.EndState;
import com.teksystems.qe.automata.sample.amazon.app.AmazonApplication;
import com.teksystems.qe.automata.sample.amazon.data.AmazonUser;
import com.teksystems.qe.automata.sample.amazon.data.Item;
import com.teksystems.qe.automata.sample.amazon.data.ItemStatus;

public class SampleTest {

	/*
	 * Sample test case that launches a browser, goes to google and searches for 'automata'
	 * and verified the results return a link to https://en.wikipedia.org/wiki/Automata_theory
	 */
	@Test
	public void sampleTest() throws Exception{

		AmazonUser user       = new AmazonUser();
		Item buyMe            = new Item();
		buyMe.setItemName("TurboTax 2016 Basic");
		
		user.addItemToBuy(buyMe);
        //create an instance of the application
		AmazonApplication app = new AmazonApplication(
				getDriver(), 
				user
				);

        app.process(new EndState(){
            @Override
            public boolean stop(Application application, BaseView view, String state) {
                return (buyMe.getStatus() == ItemStatus.VIEWING);
            } 
        });
	}

	/**
	 * Creates a new webdriver instance for your test.
	 * @return WebDriver that has been configured to execute the specified platform and browser.
	 * @throws MalformedURLException
	 */
	private WebDriver getDriver() throws MalformedURLException{
		URL server 							= new URL("http://localhost:4444/wd/hub");
		DesiredCapabilities capababilities 	= new DesiredCapabilities();
		
		// Set your OS (platform) here.
		capababilities.setPlatform(Platform.MAC);
		// Set your Browser here.
		capababilities.setBrowserName("firefox");
		// Set your Browser version here. This should be a number.
		capababilities.setVersion("47");
		capababilities.setJavascriptEnabled(true);
		
		RemoteWebDriver ret = new RemoteWebDriver(server, capababilities);
		ret.navigate().to("https://www.amazon.com/");
		return ret;
	}

}