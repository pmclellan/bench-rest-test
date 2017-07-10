# Bench Rest Test

This is my solution to the Bench Rest Test assignment.

### Installation

This is a Java 8 implementation set up to be built using Maven. Once you have downloaded the source code to your computer simply navigate to the root folder and execute the following Maven command:

    mvn package

This will generate the necessary executable JAR in your `'<project-root-dir>/target'` directory.

### Execution
Once the necessary JAR file has been generated (see *'Installation'* section) the test can be run by executing the following command:

    java -jar <project-root-dir>/target/rest-test-1.0.0.jar

A log file called `'rest-test.log'` will be generated in the directory from which this command is run. In the event of any test failures this file will provide error details and some basic debug information.
