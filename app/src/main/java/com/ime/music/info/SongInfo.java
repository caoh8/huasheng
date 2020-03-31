package com.ime.music.info;

public class SongInfo extends PlayInfo {
    private String fileName = "";
    private String fileHash = "";
    private String songName = "";
    private String singerName = "";
    private String musicUrl = "";
    private String imgUrl = "";
    private String extname = "";
    private String albumAudioID = "";
    private String feetype = "0";//鉴权获取
    private String albumID = "";
    private String segment = ""; //歌词片段

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getExtname() {
        return extname;
    }

    public void setExtname(String extname) {
        this.extname = extname;
    }

    public String getAlbumAudioID() {
        return albumAudioID;
    }

    public void setAlbumAudioID(String albumAudioID) {
        this.albumAudioID = albumAudioID;
    }

    public String getFeetype() {
        return feetype;
    }

    public void setFeetype(String feetype) {
        this.feetype = feetype;
    }

    public String getAlbumID() {
        return albumID;
    }

    public void setAlbumID(String albumID) {
        this.albumID = albumID;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    @Override
    public String getName() {
        return songName + " - " + singerName;
    }
}
