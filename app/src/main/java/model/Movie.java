package model;

import java.io.Serializable;

public class Movie implements Serializable {
    private String id; 
    private String title;
    private String studio;
    private String poster;
    private String criticsRating;

    // Default CTOR
    public Movie() {
    }

    // Parameterized CTOR
    public Movie(String id, String title, String studio, String poster, String criticsRating) {
        this.id = id;
        this.title = title;
        this.studio = studio;
        this.poster = poster;
        this.criticsRating = criticsRating;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getCriticsRating() {
        return criticsRating;
    }

    public void setCriticsRating(String criticsRating) {
        this.criticsRating = criticsRating;
    }
}
