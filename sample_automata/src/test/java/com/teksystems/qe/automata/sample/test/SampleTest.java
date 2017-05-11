package com.teksystems.qe.automata.sample.test;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.teksystems.qe.automata.sample.app.GoogleApplication;
import com.teksystems.qe.automata.sample.data.GenericDataObject;
import com.teksystems.qe.automata.sample.views.SearchResults;

public class SampleTest {

	/*
	 * Sample test case that launches a browser, goes to google and searches for 'automata'
	 * and verified the results return a link to https://en.wikipedia.org/wiki/Automata_theory
	 */
	@Test()
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
	
	/*
	 * Sample data driven test case that launches a browser, goes to google and searches for a particular
	 * string (searchString) and verifies it against an expected URL (resultsUrl) that should have been return 
	 * from the google search. The string to search for and the URL to verify the results against come from
	 * a data provider.
	 */
	@Test(dataProvider = "search-data")
	public void sampleDataDrivenTest(String searchString, String resultsUrl) throws Exception{
		//Data however you want to set it up
				GenericDataObject data = new GenericDataObject();
				data.setQuery(searchString);
				
				//create an instance of the application
				GoogleApplication app = new GoogleApplication(
						getDriver(), 
						data
						);
				
				//run until a page.
				SearchResults results = app.runUntilView(SearchResults.class);

				Assert.assertNotNull(
						results.findResultByUrl(resultsUrl),
						"Expected to find the proper result");
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
		capababilities.setBrowserName("chrome");
		// Set your Browser version here. This should be a number.
		capababilities.setVersion("58");
		capababilities.setJavascriptEnabled(true);
		return new RemoteWebDriver(server, capababilities);
	}
	
	
	@DataProvider(name="search-data")
	public Object[][] getSearchData(){
	
		Object[][] data = new Object[2][2];
		
		data[0][0] = "automata";
		data[0][1] = "https://en.wikipedia.org/wiki/Automata_theory";
		
		data[1][0] = "sd qe meetup";
		data[1][1] = "https://www.meetup.com/San-Diego-Quality-Engineering-User-Group/";
		
		return data;
	}
	
}