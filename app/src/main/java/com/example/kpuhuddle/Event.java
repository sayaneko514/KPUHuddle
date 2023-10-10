package com.example.kpuhuddle;

import java.util.Comparator;

public class Event {

    String eventID, eventName, eventType, eventLocation, eventDate, eventTime, eventHost, eventCount, eventDesc, pUrl;

    public Event() {
    }

    public Event(String eventID, String eventName, String eventType, String eventLocation, String eventDate, String eventTime, String eventHost, String eventCount, String eventDesc, String pUrl) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventType = eventType;
        this.eventLocation = eventLocation;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventHost = eventHost;
        this.eventCount = eventCount;
        this.eventDesc = eventDesc;
        this.pUrl = pUrl;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventHost() {
        return eventHost;
    }

    public void setEventHost(String eventHost) {
        this.eventHost = eventHost;
    }

    public String getEventCount() {
        return eventCount;
    }

    public void setEventCount(String eventCount) {
        this.eventCount = eventCount;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getpUrl() {
        return pUrl;
    }

    public void setpUrl(String pUrl) {
        this.pUrl = pUrl;
    }

    //sort
    public static final Comparator<Event> By_NAME_ASCENDING = new Comparator<Event>() {
        @Override
        public int compare(Event o1, Event o2) {
            return o1.getEventName().compareTo(o2.getEventName());
        }
    };

    public static final Comparator<Event> By_NAME_DESCENDING = new Comparator<Event>() {
        @Override
        public int compare(Event o1, Event o2) {
            return o2.getEventName().compareTo(o1.getEventName());
        }
    };

    public static final Comparator<Event> By_TYPE_ASCENDING = new Comparator<Event>() {
        @Override
        public int compare(Event o1, Event o2) {
            return o1.getEventType().compareTo(o2.getEventType());
        }
    };

    public static final Comparator<Event> By_TYPE_DESCENDING = new Comparator<Event>() {
        @Override
        public int compare(Event o1, Event o2) {
            return o2.getEventType().compareTo(o1.getEventType());
        }
    };
}
