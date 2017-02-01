# Sample project for automata
This is a sample project that uses Google as the application under test.

## Setup
1. Download and start the selenium server stand alone
⋅⋅* You can download the stand alone selenium server here (please note that this example was tested with selenium 2.53), http://selenium-release.storage.googleapis.com/index.html?path=2.53/
2. Start the selenium standalone server by running the following command
⋅⋅* java -jar <path to selenium server standalone jar>
⋅⋅* Example: java -jar selenium-server-standalone-2.53.1.jar
3. Ensure that you have built and have the automata jar installed in your local maven repository. For instructions on how to do this please review the automata README.
4. Update the SampleTest to match your OS, Browser and Browser version.
```java
		capababilities.setPlatform(Platform.MAC);
		capababilities.setBrowserName("firefox");
		capababilities.setVersion("38"); 
```

## Run
  mvn clean test
