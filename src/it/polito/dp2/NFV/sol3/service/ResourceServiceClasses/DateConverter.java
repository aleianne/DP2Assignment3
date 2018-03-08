package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateConverter {
	
	private String dateFormatString = "yyyy-MM-dd'T'HH:mm:ss";
	private SimpleDateFormat dateFormat;
	
	public DateConverter() {
		dateFormat = new SimpleDateFormat(dateFormatString);
	}
	
	// TODO to be changed the date format
	public XMLGregorianCalendar getCurrentXmlDate() throws DatatypeConfigurationException  {
        dateFormat.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));

        XMLGregorianCalendar xmlcal = DatatypeFactory.newInstance()
            .newXMLGregorianCalendar(
                dateFormat.format(new Date()));
        xmlcal.setTimezone(0);

        return xmlcal;
	}
	
	public Calendar convertCalendar(String stringDate) {
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(dateFormat.parse(stringDate));
			return cal;
		} catch(ParseException pe) {
			return null;
		}
	}	
	
	// this function compare the 
	public boolean compareCalendar(Calendar inputDate1, Calendar inputDate2) {
		
		if(inputDate1.compareTo(inputDate2) <= 0) 
			return true;
		else 
			return false;
	}
	
	// convert a xml gregorian calendar date to a calendar instance
	public Calendar fromXMLGregorianCalendar(XMLGregorianCalendar xc)
			 throws DatatypeConfigurationException {
		 Calendar c = Calendar.getInstance();
		 c.setTimeInMillis(xc.toGregorianCalendar().getTimeInMillis());
		 return c;
	}
	
	// convert a calendat instance into a gregorian calendar object
	public XMLGregorianCalendar toXMLGregorianCalendar(Calendar c)
			 throws DatatypeConfigurationException {
		 GregorianCalendar gc = new GregorianCalendar();
		 gc.setTimeInMillis(c.getTimeInMillis());
		 XMLGregorianCalendar xc = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
		 return xc;
	}
	
}
