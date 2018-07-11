import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class StarParser extends DefaultHandler {

    HashMap<String,Star> myStars;

    private String tempVal;

    //to maintain context
    private Star tempStar;

    public StarParser() {
        myStars = new HashMap<String,Star>();
        tempVal = "";
    }
    public HashMap<String,Star> parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("actors63.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        return myStars;
    }
    public void startElement(String uri, String localName, String qName,
    		Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            //create a new instance of star
            tempStar = new Star();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {


        if (qName.equalsIgnoreCase("actor")) {
            //add it to the list
            myStars.put(tempStar.getStageName(),tempStar);

        } else if (qName.equalsIgnoreCase("stagename")) {
            tempStar.setStageName(tempVal);
        } else if (qName.equalsIgnoreCase("dob")) {
        	tempStar.setDOB(tempVal);
            
        }

    }
}