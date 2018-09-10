package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;


import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateConverter {

    private static final String dateFormatString = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String inputDateFormatString = "yyyy-MM-dd'T'HH:mm:ssZ";

    public DateConverter() {
    }

    // TODO to be changed the date format
    public XMLGregorianCalendar getCurrentXmlDate() throws DatatypeConfigurationException {
//        DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
//        int utcOffset = TimeZone.getDefault().getRawOffset();

        GregorianCalendar todayDate = new GregorianCalendar();
        todayDate.setTime(new Date());

        return DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(todayDate);
    }

    public XMLGregorianCalendar convertCalendar(String stringDate) throws ParseException, DatatypeConfigurationException {
        DateFormat dateFormat = new SimpleDateFormat(inputDateFormatString);

        // create a new calendar
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateFormat.parse(stringDate));

        return toXMLGregorianCalendar(cal);
    }

//	// this function compare the
//	public boolean compareCalendar(Calendar inputDate1, Calendar inputDate2) {
//		return inputDate1.compareTo(inputDate2) <= 0;
//	}

    // convert a xml gregorian calendar date to a calendar instance
    public Calendar fromXMLGregorianCalendar(XMLGregorianCalendar xc)
            throws DatatypeConfigurationException {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(xc.toGregorianCalendar().getTimeInMillis());
        return c;
    }

    // convert a calendar instance into a gregorian calendar object
    public XMLGregorianCalendar toXMLGregorianCalendar(Calendar c)
            throws DatatypeConfigurationException {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(c.getTimeInMillis());
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
    }

}
