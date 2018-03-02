package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;


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
	
	public static XMLGregorianCalendar getCurrentXmlDate() throws DatatypeConfigurationException  {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));

        XMLGregorianCalendar xmlcal = DatatypeFactory.newInstance()
            .newXMLGregorianCalendar(
                dateFormat.format(new Date()));
        xmlcal.setTimezone(0);

        return xmlcal;
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
