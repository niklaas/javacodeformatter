#!/usr/bin/env sh

java -cp target/java-formatter-0.0.1-SNAPSHOT-jar-with-dependencies.jar Formatter src/main/resources/sample.opts < src/main/java/Formatter.java
