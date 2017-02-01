package com.teksystems.qe.automata.sample.test;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.teksystems.qe.automata.sample.app.GoogleApplication;
import com.teksystems.qe.automata.sample.data.GenericDataObject;
import com.teksystems.qe.automata.sample.views.SearchResults;

public class SampleTest {

	@Test
	public void sampleTest() throws Exception{
		//Data however you want to set it up
		GenericDataObject data = new GenericDataObject();
		data.setQuery("automata");
		
		//create an instance of the application
		GoogleApplication app = new GoogleApplication(
				getDriver(), 
				data
				);
		
		//run until a page.
		SearchResults results = app.runUntilView(SearchResults.class);

		Assert.assertNotNull(
				results.findResultByUrl("https://en.wikipedia.org/wiki/Automata_theory"),
				"Expected to find the proper result");

	}
	
	
	
	private WebDriver getDriver() throws MalformedURLException{
		URL server 							= new URL("http://localhost:4444/wd/hub");
		DesiredCapabilities capababilities 	= new DesiredCapabilities();
		capababilities.setPlatform(Platform.MAC);
		capababilities.setBrowserName("firefox");
		capababilities.setVersion("38");
		capababilities.setJavascriptEnabled(true);
		return new RemoteWebDriver(server, capababilities);
	}
}
