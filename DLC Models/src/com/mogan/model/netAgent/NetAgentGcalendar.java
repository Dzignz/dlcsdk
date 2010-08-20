package com.mogan.model.netAgent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.google.gdata.client.GoogleService;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.data.calendar.ColorProperty;
import com.google.gdata.data.calendar.HiddenProperty;
import com.google.gdata.data.calendar.SelectedProperty;
import com.google.gdata.data.calendar.TimeZoneProperty;
import com.google.gdata.data.extensions.Where;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.mogan.face.NetAgentModel;

public class NetAgentGcalendar extends NetAgentModel {
	private static Logger logger = Logger.getLogger(NetAgentGcalendar.class.getName() );
	// 基本網址
	private static final String METAFEED_URL_BASE = "http://www.google.com/calendar/feeds/";

	// 程式開啟的日曆網址，由基本網址加上使用者參數組合而成
	private URL metafeedUrl = null;

	// The URL for the allcalendars feed of the specified user.
	// (e.g. http://www.googe.com/feeds/calendar/jdoe@gmail.com/allcalendars/full)
	private URL allcalendarsFeedUrl = null;

	// The URL for the owncalendars feed of the specified user.
	// (e.g. http://www.googe.com/feeds/calendar/jdoe@gmail.com/owncalendars/full)
	private URL owncalendarsFeedUrl = null;

	// The string to add to the user's metafeedUrl to access the allcalendars
	// feed.
	private static final String ALLCALENDARS_FEED_URL_SUFFIX = "/allcalendars/full";

	// The string to add to the user's metafeedUrl to access the owncalendars
	// feed.
	private static final String OWNCALENDARS_FEED_URL_SUFFIX = "/owncalendars/full";

	// The HEX representation of red, blue and green
	private static final String RED = "#A32929";
	private static final String BLUE = "#2952A3";
	private static final String GREEN = "#0D7813";	
	
	
	private String userName = "";
	private String userPassword = "";
	private CalendarService service = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		NetAgentGcalendar nag = new NetAgentGcalendar("elgoogdian@gmail.com",
				"vfbyfnfvygo");

		logger.info("Calendars in metafeed");
		try {
			nag.printUserCalendars(nag.metafeedUrl);
		    //  CalendarEntry newCalendar = nag.createCalendar();
		    //  CalendarEntry updatedCalendar = nag.updateCalendar(newCalendar);
		      
		      CalendarEntry newSubscription = nag.createSubscription();
		      CalendarEntry updatedSubscription = nag.updateSubscription(newSubscription);
		      
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * printUserCalendars(service, metafeedUrl); logger.info("Calendars in allcalendars feed"); printUserCalendars(service,
		 * allcalendarsFeedUrl); logger.info("Calendars in owncalendars feed"); printUserCalendars(service, owncalendarsFeedUrl);
		 */
	}

	public NetAgentGcalendar(String userName, String userPassword) {
		this.userName = userName;
		this.userPassword = userPassword;

		try {
			this.metafeedUrl = new URL(METAFEED_URL_BASE + userName);
			this.allcalendarsFeedUrl = new URL(METAFEED_URL_BASE + userName
					+ ALLCALENDARS_FEED_URL_SUFFIX);
			this.owncalendarsFeedUrl = new URL(METAFEED_URL_BASE + userName
					+ OWNCALENDARS_FEED_URL_SUFFIX);
		} catch (MalformedURLException e) {
			// Bad URL
			System.err.println("Uh oh - you've got an invalid URL.");
			e.printStackTrace();
			return;
		}

		service = new CalendarService("demo-CalendarFeedDemo-1");
		try {
			service.setUserCredentials(userName, userPassword);
		} catch (AuthenticationException e) {
			// Invalid credentials
			e.printStackTrace();
		}

	}

	/**
	 * Prints the titles of calendars in the feed specified by the given URL.
	 * 
	 * @param service An authenticated CalendarService object.
	 * @param feedUrl The URL of a calendar feed to retrieve.
	 * @throws IOException If there is a problem communicating with the server.
	 * @throws ServiceException If the service is unable to handle the request.
	 */
	private void printUserCalendars(URL feedUrl) throws IOException,
			ServiceException {

		// Send the request and receive the response:
		CalendarFeed resultFeed = this.service.getFeed(feedUrl,
				CalendarFeed.class);

		// Print the title of each calendar
		for (int i = 0; i < resultFeed.getEntries().size(); i++) {
			CalendarEntry entry = resultFeed.getEntries().get(i);
			logger.info("\t" + entry.getTitle().getPlainText());
		}
	}
	
	  /**
	   * Creates a new secondary calendar using the owncalendars feed.
	   * 
	   * @param service An authenticated CalendarService object.
	   * @return The newly created calendar entry.
	   * @throws IOException If there is a problem communicating with the server.
	   * @throws ServiceException If the service is unable to handle the request.
	   */
	  private CalendarEntry createCalendar()
	      throws IOException, ServiceException {
	    logger.info("Creating a secondary calendar");

	    // Create the calendar
	    CalendarEntry calendar = new CalendarEntry();
	    calendar.setTitle(new PlainTextConstruct("Little League Schedule"));
	    calendar.setSummary(new PlainTextConstruct(
	        "This calendar contains the practice schedule and game times."));
	    calendar.setTimeZone(new TimeZoneProperty("Asia/Taipei"));
	    calendar.setHidden(HiddenProperty.FALSE);
	    calendar.setColor(new ColorProperty(BLUE));
	    calendar.addLocation(new Where("", "", "Oakland"));

	    // Insert the calendar
	    return service.insert(owncalendarsFeedUrl, calendar);
	  }	

	  /**
	   * Updates the title, color, and selected properties of the given calendar
	   * entry using the owncalendars feed. Note that the title can only be updated
	   * with the owncalendars feed.
	   * 
	   * @param calendar The calendar entry to update.
	   * @return The newly updated calendar entry.
	   * @throws IOException If there is a problem communicating with the server.
	   * @throws ServiceException If the service is unable to handle the request.
	   */
	  private CalendarEntry updateCalendar(CalendarEntry calendar)
	      throws IOException, ServiceException {
	    logger.info("Updating the secondary calendar"+calendar.getId());

	    calendar.setTitle(new PlainTextConstruct("New title"));
	    calendar.setColor(new ColorProperty(GREEN));
	    calendar.setSelected(SelectedProperty.TRUE);
	    return calendar.update();
	  }
	  
	  /**
	   * Deletes the given calendar entry.
	   * 
	   * @param calendar The calendar entry to delete.
	   * @throws IOException If there is a problem communicating with the server.
	   * @throws ServiceException If the service is unable to handle the request.
	   */
	  private void deleteCalendar(CalendarEntry calendar)
	      throws IOException, ServiceException {
	    logger.info("Deleting the secondary calendar");

	    calendar.delete();
	  }	  
	  
	  /**
	   * Subscribes to the public Google Doodles calendar using the allcalendars
	   * feed.
	   * 
	   * @param service An authenticated CalendarService object.
	   * @return The newly created calendar entry.
	   * @throws IOException If there is a problem communicating with the server.
	   * @throws ServiceException If the service is unable to handle the request.
	   */
	  private CalendarEntry createSubscription()
	      throws IOException, ServiceException {
	    logger.info("Subscribing to the Google Doodles calendar");

	    CalendarEntry calendar = new CalendarEntry();
	    calendar.setId("6anunud5suvvk3lkl7j1d2d3js@group.calendar.google.com");

	    return service.insert(allcalendarsFeedUrl, calendar);
	  }
	  
	  /**
	   * Updated the color property of the given calendar entry.
	   * 
	   * @param calendar The calendar entry to update.
	   * @return The newly updated calendar entry.
	   * @throws IOException If there is a problem communicating with the server.
	   * @throws ServiceException If the service is unable to handle the request.
	   */
	  private CalendarEntry updateSubscription(CalendarEntry calendar)
	      throws IOException, ServiceException {
	    logger.info("Updating the display color of the Doodles calendar");

	    calendar.setColor(new ColorProperty(RED));
	    return calendar.update();
	  }
	  
	  /**
	   * Deletes the given calendar entry.
	   * 
	   * @param calendar The calendar entry to delete
	   * @throws IOException If there is a problem communicating with the server.
	   * @throws ServiceException If the service is unable to handle the request.
	   */
	  private void deleteSubscription(CalendarEntry calendar)
	      throws IOException, ServiceException {
	    logger.info("Deleting the subscription to the Doodles calendar");

	    calendar.delete();
	  }	  
}
