# emailcruncher

emailcruncher is a graphical tool to grab emails from web sites. This application was originally designed as an exercise 
to build a graphical user interface using the Matisse ui generator provided by Netbeans.

Features:  
* support http and https protocols 
* skip or ignore some emails/sites address
* keep only valid emails 
* set the number of concurrent crawler threads 
* search on different (external) sites from the original site
* set the local depth search
* set the external depth search
* export found emails to txt or xls formats

Note. the crawler can be heavily multi-threaded. Beware not to overload web sites.

## Build 
 
Java 11+ is mandatory to build emailcruncher.

```
mvn clean install
```

## Launch 

```
java -jar emailcruncher.jar
```

Note. the name of resuting jar depends on the current application version.

## Contribute

Contributions are welcome.

1. Fork the project on Github (git clone ...)
2. Create a local feature branch (git checkout -b newFeature)
3. Commit modifications on the local feature branch (git commit -am "new modification")
4. Push the local branch (git push origin newFeature)
5. Create a pull request

## Licence

emailcruncher is provided under GPL v3 licence.

## Copyright

Copyright Emmanuel Deviller (c) 2010-2020
