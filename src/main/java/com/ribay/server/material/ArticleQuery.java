package com.ribay.server.material;

import java.util.List;

/**
 * Created by CD on 01.05.2016.
 */
public class ArticleQuery {

    private String text;
    private Boolean movie;
    private List<String> genre;
    private Integer price_low;
    private Integer price_high;
    private Integer rating_low;
    private Integer rating_high;
    private Integer votes_low;
    private Integer votes_high;

    // TODO add order by

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean isMovie() {
        return movie;
    }

    public void setMovie(Boolean movie) {
        this.movie = movie;
    }

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public Integer getPrice_low() {
        return price_low;
    }

    public void setPrice_low(Integer price_low) {
        this.price_low = price_low;
    }

    public Integer getPrice_high() {
        return price_high;
    }

    public void setPrice_high(Integer price_high) {
        this.price_high = price_high;
    }

    public Integer getRating_low() {
        return rating_low;
    }

    public void setRating_low(Integer rating_low) {
        this.rating_low = rating_low;
    }

    public Integer getRating_high() {
        return rating_high;
    }

    public void setRating_high(Integer rating_high) {
        this.rating_high = rating_high;
    }

    public Integer getVotes_low() {
        return votes_low;
    }

    public void setVotes_low(Integer votes_low) {
        this.votes_low = votes_low;
    }

    public Integer getVotes_high() {
        return votes_high;
    }

    public void setVotes_high(Integer votes_high) {
        this.votes_high = votes_high;
    }

}
