package com.ime.music.info;

abstract public class PlayInfo {
    protected String playUrl;
    protected boolean isPlayed = false;
    abstract public String getName();
//    abstract public String getFileName();
//    abstract public String getAuthorName();

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setPlayed(boolean played) {
        isPlayed = played;
    }
}
