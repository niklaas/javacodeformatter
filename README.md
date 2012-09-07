Introduction
------------

The Java code here uses Eclipse's code formatting capability to format Java
code. You don't need Eclipse installed to use it; the build bundles all of the
Eclipse libraries necessary.

Building
--------

    $ mvn package

Running
-------

    $ java -cp java-formatter-jar-with-dependencies.jar sample.opts

The only required argument is a file listing code formatter options for
Eclipse's `DefaultCodeFormatterOptions` class that should be applied over the
top of the standard Java settings. Each line in the file should be:

    <field name>:<type>:<value>

A blank file ought to be fine. Comment lines starting with '#' and blank lines
are ignored.

When run like the above, the formatter reads code from standard input.
Formatted code is written to standard output.

    $ java -cp java-formatter-jar-with-dependencies.jar sample.opts MyFile.java

You can pass a file name instead of using standard input.

    $ java -cp java-formatter-jar-with-dependencies.jar sample.opts src/java

If you pass a directory, the formatter will recursively format all Java files
in the directory. Formatted code is written to new files prefixed with
"formatted_", e.g., formatted_MyFile.java is the formatted form of MyFile.java.

Credits
-------

The formatter is heavily based on work published here:

https://ssscripting.wordpress.com/2009/06/10/how-to-use-the-eclipse-code-formatter-from-your-code/
