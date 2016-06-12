package com.teksystems.qe.automata;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.teksystems.qe.automata.sample.TestApplication;
import com.teksystems.qe.automata.sample.TestHomePage;
import com.teksystems.qe.automata.sample.TestSecondPage;

public class ApplicationTest {

	@Test
	public void basicTest() throws ViewInitializationException, ViewNotDefinedException{
		TestApplication app = new TestApplication();
		Assert.assertTrue(app.getCurrentView() instanceof TestHomePage);
	}
	
	@Test
	public void listenerTest() throws Exception{
		TestApplication app = new TestApplication();
		app.runUntilView(TestSecondPage.class);
	}
	
}
