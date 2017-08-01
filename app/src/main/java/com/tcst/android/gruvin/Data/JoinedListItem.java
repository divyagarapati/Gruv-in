package com.tcst.android.gruvin.Data;

/**
 * Created by Prasanthi on 01-02-2017.
 */
public class JoinedListItem {
    public String joineventtitle;
    public String joineventdate;
    public String joineventtime;
    public JoinedListItem(String joineventtitle,String joineventdate,String joineventtime) {
        this.joineventtitle=joineventtitle;
        this.joineventdate=joineventdate;
        this.joineventtime=joineventtime;


    }
    public void setJoineventtitle(String joineventtitle){
        this.joineventtitle=joineventtitle;
    }
    public String getJoineventtitle(){
        return joineventtitle;
    }

    public void setJoineventdate(String joineventdate){
        this.joineventdate=joineventdate;
    }
    public String getJoineventdate(){
        return joineventdate;
    }

    public void setJoineventtime(String joineventtime){
        this.joineventtime=joineventtime;
    }
    public String getJoineventtime(){
        return joineventtime;
    }
}
