package com.tcst.android.gruvin.Data;

/**
 * Created by Prasanthi on 30-12-2016.
 */
public class EventListItem {
    public String eventtitle;
    public String eventdate;
    public String eventtime;
    public String eventinvitation;
    private String eventnull;
    private String timenull;


    public EventListItem(String eventtitle,String eventdate,String eventtime,String eventinvitation,String eventnull,String timenull) {
        this.eventtitle=eventtitle;
        this.eventdate=eventdate;
        this.eventtime=eventtime;
        this.eventinvitation=eventinvitation;
        this.eventnull=eventnull;
        this.timenull=timenull;

    }


    public void setEventtitle(String eventtitle){
        this.eventtitle=eventtitle;
    }
    public String getEventtitle(){
        return eventtitle;
    }

    public void setEventdate(String eventdate){
        this.eventdate=eventdate;
    }
    public String getEventdate(){
        return eventdate;
    }

    public void setEventtime(String eventtime){
        this.eventtime=eventtime;
    }
    public String getEventtime(){
        return eventtime;
    }

    public void setEventinvitation(String eventinvitation){
        this.eventinvitation=eventinvitation;
    }
    public String getEventinvitation(){
        return eventinvitation;
    }
}
