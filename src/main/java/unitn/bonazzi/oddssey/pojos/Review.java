package unitn.bonazzi.oddssey.pojos;

import java.util.Date;

public class Review {
    private String text;
    private int rating;
    private String author;
    private Date date;

    public Review() {
        this.text = "";
        this.rating = 0;
        this.author = "";
        this.date = new Date();
    }

    public Review(String text, int rating, String author, Date date) {
        this.text = text;
        this.rating = rating;
        this.author = author;
        this.date = date;
    }

    public Review(String text, int rating, String author) {
        this.text = text;
        this.rating = rating;
        this.author = author;
        this.date = new Date();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
