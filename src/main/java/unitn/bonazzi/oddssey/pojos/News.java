package unitn.bonazzi.oddssey.pojos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class News {
    private String title;
    private String content;
    private LocalDate date;

    public News(String title, String content, LocalDate date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public News(String title) {
        this.title = title;
        this.date = LocalDate.now();
        this.content = title;
    }

    public News() {
    }

    public News(String title, String content) {
        this.title = title;
        this.content = content;
        this.date = LocalDate.now();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getDate() {
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String text = date.format(formatters);
        return LocalDate.parse(text, formatters);
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
