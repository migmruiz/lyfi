# LyricsFynder

The purpose of this program is to make a text search
engine for searching inside lyrics of a personal music
library's musics.


## Libraries used 

The following libraries were used in this project,
the first three are Apache Software Foundation's projects.

* [Apache Lucene Core](http://lucene.apache.org/core/)
* [Apache Commons CLI](http://commons.apache.org/cli/)
* [Apache HttpComponents' HttpClient](http://hc.apache.org/httpcomponents-client-ga/)
* [Jaudiotagger](http://www.jthink.net/jaudiotagger/)
* [lyrics](https://github.com/mariosangiorgio/lyrics)


## References

### sourceforge

* [D. Cutting - Lucene lecture at Pisa](http://lucene.sourceforge.net/talks/pisa/)

### developerWorks

* [A. Sonawane - Using Apache Lucene to search text](http://www.ibm.com/developerworks/java/library/os-apache-lucenesearch/)
* [D. P. Zhou - Delve inside the Lucene indexing mechanism](http://www.ibm.com/developerworks/library/wa-lucene/)

### github

* [M. Sangiorgio - LibraryExplorer](https://github.com/mariosangiorgio/lyrics/blob/master/src/main/java/lyrics/libraryExplorer/LibraryExplorer.java)

## Known issues

* Analyzes only the given data folder, not it's children

## Wish-list

* Graphical user interface
* Wider query parsing.
* implement playback capabilities

## Build

    ../lyfi$ ant 

if you experience some ${ECLIPSE_HOME} issues try to set a ECLIPSE_HOME variable pointing to your Eclipse 3.7.2:
    
    ../lyfi$ ECLIPSE_HOME=$HOME/.local/opt/eclipse
    ../lyfi$ ant -Declipse.home=$ECLIPSE_HOME

actually all you need is org.junit_4.8.2.v4_8_2_v20110321-1705/junit.jar and org.hamcrest.core_1.1.0.v20090501071000.jar inside a folder called plugins inside $ECLIPSE_HOME. Don't worry, I will mavenize it soon.
