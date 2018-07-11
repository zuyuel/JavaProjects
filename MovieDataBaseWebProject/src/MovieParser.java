import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MovieParser extends DefaultHandler {

    HashMap<String, Movie> myMovies;

    private String tempVal;

    //to maintain context
    private Movie tempMovie;

    public MovieParser() {
        myMovies = new HashMap<String, Movie>();
        tempVal = "";
    }

    public HashMap<String, Movie> parseDocument() {
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();
            //parse the file and also register this class for call backs
            sp.parse("mains243.xml", this);
 
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        return myMovies;
    }
    public void startElement(String uri, String localName, String qName,
    		Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of movie
            tempMovie = new Movie();
        }
    }
    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("film")) {
            //add it to the list
            myMovies.put(tempMovie.getId(), tempMovie);
        } else if (qName.equalsIgnoreCase("fid")) {
            tempMovie.setId(tempVal);
        } else if (qName.equalsIgnoreCase("t")) {
            tempMovie.setTitle(tempVal);
        } else if (qName.equalsIgnoreCase("year")) {
            tempMovie.setYear(tempVal);
        } else if (qName.equalsIgnoreCase("dirn")) {
        	tempMovie.addDirector(tempVal);
        } else if (qName.equalsIgnoreCase("cat")) {
        	tempMovie.addGenre(tempVal);
        }     
    }
}