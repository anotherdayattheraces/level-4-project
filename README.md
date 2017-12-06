# level-4-project
Entity Retrieval for Complex Answer Retrieval

This system is intended to be an entity retrieval engine.

To use the system you will need:

JDK (Java development kit) get it here: http://www.oracle.com/technetwork/java/javase/downloads/jdk9-downloads-3848520.html
Apache maven get it here: https://maven.apache.org/download.cgi

Once you have installed these and cloned this repository you are going to need to build both Galago and the entity linker individually, do this by typing mvn install -DskipTests on a terminal in each respective directory. (Do this for galago first).

Now you need the data - first download and unzip the pubmed article files from here (labelled pmc.blah.tar.gz: http://www.trec-cds.org/2016.html
These files are very large in size so you may not be able to use all of them, but the more files you have the more complete your system will be.
Now you need the files from Dbpedia, get them from here: http://wiki.dbpedia.org/downloads-2016-10 download the relevant .ttl files from under the "datasets" heading.
