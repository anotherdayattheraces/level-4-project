package org.lemurproject.galago.core.parse;

import org.lemurproject.galago.core.types.DocumentSplit;
import org.lemurproject.galago.utility.Parameters;
import sun.rmi.runtime.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PubMedParser extends DocumentStreamParser{
    BufferedReader reader;
    String identifier;
    public PubMedParser(DocumentSplit split, Parameters parameters) throws IOException {
        super(split, parameters);
//          Parameters parameters, String fileName, BufferedReader bufferedReader) {
        this.identifier = getIdentifier(parameters, getFileName(split));
        this.reader = getBufferedReader(split);
    }
    public String getIdentifier(Parameters parameters, String fileName) {
        String idType = parameters.get("identifier", "filename");
        if (idType.equals("filename")) {
            return fileName;
        } else {
            String id = stripExtensions(fileName);
            id = new File(id).getName();
            return id;
        }
    }
    public static String stripExtension(String name, String extension) {
        if (name.endsWith(extension)) {
            name = name.substring(0, name.length() - extension.length());
        }
        return name;
    }

    public static String stripExtensions(String name) {
        name = stripExtension(name, ".bz");
        name = stripExtension(name, ".bz2");
        name = stripExtension(name, ".gz");
        name = stripExtension(name, ".html");
        name = stripExtension(name, ".xml");
        name = stripExtension(name, ".txt");
        return name;
    }

    @Override
    public Document nextDocument() throws IOException {
        if (reader == null || !reader.ready()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        String line;
        if (reader == null || null == waitFor("<PubmedArticle>")) {
            return null;
        }
        while ((line = reader.readLine()) != null) {
            if (line.contains("</PubmedArticle>")) {
                break;
            }
            builder.append(line);
            builder.append('\n');
        }
         int start = builder.indexOf("<PMID") + 5;
         int end = builder.indexOf("</PMID>");
         String identifier = builder.substring(start,end).trim();
         identifier = "Article "+identifier.substring(identifier.lastIndexOf('>')+1);
         String title = getTitle(builder.toString());
         if (identifier == null) {
            return null;
         }
         Document result = new Document(identifier, builder.toString());
         //result.metadata.put("title", title);
         return result;
    }

    public static String getTitle(String text) {
        try {
            int start = text.indexOf("<title>");
            if (start < 0) {
                return "";
            }
            int end = text.indexOf("</title>", start);
            if (end < 0) {
                return "";
            }
            return new String(text.substring(start + "<title>".length(), end));
        } catch (StringIndexOutOfBoundsException ex) {
            return "";
        }
    }

    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
            reader = null;
        }

    }

    public String waitFor(String tag) throws IOException {
        String line;
        while((line=reader.readLine())!=null){
            if(line.contains(tag)){
                return line;
            }
        }
        return null;
    }


}
