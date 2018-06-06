package com.nsh.catchmusic.model;

/**
 * Created by ThisIsNSH on 6/6/2018.
 */

public class Song {
    String name,url,album;
    public Song(){
    }
    public Song(String name, String url, String album){
        this.name = name;
        this.url = url;
        this.album = album;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getUrl() {

        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
