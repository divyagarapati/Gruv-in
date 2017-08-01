package com.tcst.android.gruvin.Data;


public class AudioItem {
    public String audioDuration;
    public String audioUrl;
    public String audioname;
    public String songid;
    public boolean selected = false;

    public AudioItem(String audioDuration, String audioUrl, String audioname, String songid, boolean selected) {
        this.audioDuration=audioDuration;
        this.audioUrl=audioUrl;
        this.audioname=audioname;
        this.songid=songid;
        this.selected=selected;
    }


    public void setAudioname(String audioname){
        this.audioname=audioname;
    }
    public String getAudioname(){
        return audioname;
    }
    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public void setAudioDuration(String audioUrl){
        this.audioUrl=audioUrl;
    }
    public String getAudioDuration(){
        return audioDuration;
    }
    public String getAudioUrl(){
        return audioUrl;
    }
    public void setSongid(String songid){
        this.songid=songid;
    }
    public String getSongid(){
        return songid;
    }
}
