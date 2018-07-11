import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CastParser extends DefaultHandler 
{
    HashMap<String, Star> StarsIn;
    private String tempVal;
    private String Film;
    private String starName;

    public CastParser() {
        StarsIn = new HashMap<String, Star>();
        tempVal = "";
        Film = "";
        starName = "";
    }
    public HashMap<String, Star> parseDocument(HashMap<String, Star> updateStars) {

        //get a factory
    	StarsIn = updateStars;
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("casts124.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        return StarsIn;
    }
    public void startElement(String uri, String localName, String qName,
    		Attributes attributes) throws SAXException {
        //reset
        tempVal = "";

    }
    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }
    public void endElement(String uri, String localName, String qName) throws SAXException {

    	switch (qName.toLowerCase())
    	{
	    	case "f":
	    		Film = tempVal;
	    		break;
	    	case "a":
	    		starName = tempVal;
	    		break; 
	    	case "m":
	    		if (StarsIn.get(starName) == null)
	    		{
	    			Star newStar = new Star();
	    			newStar.setStageName(starName);
	    			StarsIn.put(starName, newStar);
	    		}
	    		StarsIn.get(starName).addMovie(Film);
	    		break;
	    	default:
	    		break;
    	}
    }
}