Introduction
------------

The Java code here uses Eclipse's code formatting capability to format Java
code. You don't need Eclipse installed to use it; the build bundles all of the
Eclipse libraries necessary.

Note: This has been udpated to support JAVA 8

Building
--------

    $ mvn package

Running
-------

    $ java -cp java-formatter-jar-with-dependencies.jar Formatter sample.opts

The only required argument is a file listing code formatter options for
Eclipse's `DefaultCodeFormatterOptions` class that should be applied over the
top of the standard Java settings. Each line in the file should be:

    <field name>:<type>:<value>

A blank file ought to be fine. Comment lines starting with '#' and blank lines
are ignored.

When run like the above, the formatter reads code from standard input.
Formatted code is written to standard output.

    $ java -cp java-formatter-jar-with-dependencies.jar Formatter sample.opts MyFile.java

You can pass a file name instead of using standard input.

    $ java -cp java-formatter-jar-with-dependencies.jar Formatter sample.opts src/java


Running against directory
--------------------------

If you pass a directory, the formatter will recursively format all Java files
in the directory. And replace them. (Original creates a "formatted_" prefix)

Useful usage?
--------------------------

We currently use this with ANT, as below (modify as needed)

	<!-- Code Beautifier, used for pre-commit hook -->
	<target name="src-beautify">
		<!-- Scan and beautify the code -->
		<exec executable="java">
			<arg value="-cp"/>
			<arg value="./build-tools/java-formatter/java-formatter-with-dependencies.jar"/>
			<arg value="Formatter"/>
			<arg value="./build-config/code-format.opts"/>
			<arg value="./src"/>
		</exec>
	</target>

Such that `ant src-beautify` is called as part of the build / commit flow. Which helps ends
all the individual programmers on the team space/tabs/brackets wars. Use whatever you want on
your own branch, master branch will however follow a standard team format.

Credits
-------

The formatter is heavily based on work published here:

https://ssscripting.wordpress.com/2009/06/10/how-to-use-the-eclipse-code-formatter-from-your-code/

And was formally turned into a jar here:

https://github.com/kbzod/javacodeformatter
