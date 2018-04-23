# simplewebcrawler

Simple CLI webcrawling tool crawl data from website and save it on the local machine with it's resource file including
CSS, JS and Image files.


How to Build:

This project is built using maven tool and dependencies are updated in pom.xml.

build command
```
    mvn clean install
```
This will create a jar on  your target location

How to Run:

You need to specify url and depth parameters to run this project.

```
java -Durl=http://wikipedia.com/ -Ddepth=2 -jar target/simplewebcrawler-1.0-SNAPSHOT.jar

```

The website local copy directory will be created on the current working directory with the hostname,
We can see website resources inside this directory, it follows the same structure used in website.

Future Enhancements.

1. Search for a particular keyword and get all information relates to that website.
2. If we hit the same website frequenctly, possibility of website owner blocking our IP, keep a delay for 5 minutes.
3. Check for Robot.txt file
