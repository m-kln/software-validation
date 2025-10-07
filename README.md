# ECSE 429 - software-validation

This test suite was designed to study the behaviour of the REST API To Do List Manager available at: 
https://github.com/eviltester/thingifier/releases 

The tests were designed using a Maven build to look into the behaviours of:
- To Do Items
- Projects

Once installed, launching the application can be done using the command ```java -jar runTodoManagerRestAPI-1.5.5.jar```

The API can be viewed at http://localhost:4567/gui

To run the tests, open a terminal window and execute this command at the root of the project: ```mvn test```
