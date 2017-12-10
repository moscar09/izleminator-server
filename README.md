# izleminator-server [![Build Status](https://travis-ci.org/moscar09/izleminator-server.svg?branch=master)](https://travis-ci.org/moscar09/izleminator-server)
## About
This is the server code for the [izleminator chrome extension](https://github.com/moscar09/izleminator).
It implements the standard Java Websocket library in order to provide chatting functionality and to synchronise
the video streams of the participants in a netflix group viewing.

## How to run
1. clone the code via `https://github.com/moscar09/izleminator-server.git`
2. run `./gradlew check` to see if the tests are passing
3. start the server via `./gradlew tomcatStart` or `./gradlew jettyStart`
4. stop the server with `./gradlew appStop`

## Editing th ecode
1. Import the project in eclipse via `Import existing Gradle project`
2. Run the same gradle jobs from within eclipse or command line to test
