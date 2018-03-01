package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateConverter {
	
	public static XMLGregorianCalendar getCurrentXmlDate() throws DatatypeConfigurationException  {
		
		// this is the date format for the xml serialization
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ") { 
		    public Date parse(String source,ParsePosition pos) {    
		        return super.parse(source.replaceFirst(":(?=[0-9]{2}$)",""),pos);
		    }
		};

		// create the xml gregorian calendar date
		Date newDate = new Date();
		XMLGregorianCalendar xc = DatatypeFactory.newInstance().newXMLGregorianCalendar(df.format(newDate));
		return xc;
	}
	
	// this function compare the 
	public static boolean compareXmlGregorianCalendar(XMLGregorianCalendar xmlDate, Date date) {
		GregorianCalendar date1 = xmlDate.toGregorianCalendar();
		GregorianCalendar date2 = new GregorianCalendar();
		date2.setTime(date);
		
		int compareResult = date1.compareTo(date2);
		
		if(compareResult <= 0) 
			return true;
		else 
			return false;
	}
	
	//
	public static Calendar fromXMLGregorianCalendar(XMLGregorianCalendar xc)
			 throws DatatypeConfigurationException {
		 Calendar c = Calendar.getInstance();
		 c.setTimeInMillis(xc.toGregorianCalendar().getTimeInMillis());
		 return c;
	}
}
