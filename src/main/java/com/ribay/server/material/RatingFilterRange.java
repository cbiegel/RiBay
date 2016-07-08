package com.ribay.server.material;

/**
 * @author Chris on 08.07.2016.
 */
public class RatingFilterRange {
    private String ratingFrom;
    private String ratingTo;

    public RatingFilterRange() {
    }

    public String getRatingFrom() {
        return ratingFrom;
    }

    public void setRatingFrom(String ratingFrom) {
        this.ratingFrom = ratingFrom;
    }

    public String getRatingTo() {
        return ratingTo;
    }

    public void setRatingTo(String ratingTo) {
        this.ratingTo = ratingTo;
    }
}
