# Sample project for automata
This is a sample project that uses Google as the application under test.

## Setup & Run

### Setup Automata
1. Ensure that you have built and have the automata jar installed in your local maven repository. For instructions on how to do this please review the automata [README](https://github.com/tonyhernandez/automata/tree/master/automata).


### Setup for latest version of Chrome
We have included the latest selenium standalone server and chrome driver. 
1. Start the selenium standalone server by running the following command
  * Navigate to the automata/sample_automata directory
  * Run java -jar selenium-server-standalone-3.0.1.jar

### Setup for different version of browser
1. Download and start the selenium server stand alone
  * You can download the stand alone selenium server [here](http://selenium-release.storage.googleapis.com/index.html)
  * Please note that this example was tested with selenium driver 3.0.1 
2. Start the selenium standalone server by running the following command
  * java -jar /path/to/selenium/server/standalone/jar
  * Example: java -jar selenium-server-standalone-3.0.1.jar
  * Note: You may need to install browser drivers.
3. Update the SampleTest to match your OS, Browser and Browser version.
```java
		capababilities.setPlatform(Platform.MAC);
		capababilities.setBrowserName("chrome");
		capababilities.setVersion("58"); 
```

### Run the tests
  mvn clean test
