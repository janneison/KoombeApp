package com.jannesion.koombeaapp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class Utilidades {

	  
  public static int calculateAge(Date bornDate) {
	   Calendar cal = Calendar.getInstance(); // current date
	   int currYear = cal.get(Calendar.YEAR);
	   int currMonth = cal.get(Calendar.MONTH);
	   int currDay = cal.get(Calendar.DAY_OF_MONTH);
	   cal.setTime(bornDate); // now born date
	   int years = currYear - cal.get(Calendar.YEAR);
	   int bornMonth = cal.get(Calendar.MONTH);
	   if (bornMonth == currMonth) { // same month
	    return cal.get(Calendar.DAY_OF_MONTH) <= currDay ? years
	      : years - 1;
	   } else {
	    return bornMonth < currMonth ? years - 1 : years;
	   }
  }
  
  public static Date parseDateTime(String dateString) {
	    if (dateString == null) return null;
	    DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
	    if (dateString.contains("T")) dateString = dateString.replace('T', ' ');
	    if (dateString.contains("Z")) dateString = dateString.replace("Z", "+0000");
	    else
	        dateString = dateString.substring(0, dateString.lastIndexOf(':')) + dateString.substring(dateString.lastIndexOf(':')+1);
	    try {
	    	String[] fechaSimple = dateString.split(" ");
	        return fmt.parse(fechaSimple[0]);
	    }
	    catch (ParseException e) {
	        Log.e("error", "Could not parse datetime: " + dateString);
	        return null;
	    }
	}
	
}
