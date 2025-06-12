package unitn.bonazzi.oddssey.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import unitn.bonazzi.oddssey.pojos.News;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.time.LocalDate;
import java.util.List;

@Repository
public class NewsRepository {
    private final JdbcTemplate jdbc;

    public NewsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<News> getAllNews(){
        String sql = "SELECT * FROM news";
        RowMapper<News> rowMapper = (r, i) -> {
            News rowObject = new News();
            rowObject.setDate(r.getObject("DATE", LocalDate.class));
            rowObject.setTitle(r.getString("TITLE"));
            if(r.getString("CONTENT")!=null){
                rowObject.setContent(r.getString("CONTENT"));
            }
            return rowObject;
        };
        return jdbc.query(sql, rowMapper);
    }

    public void addNews(News news){
        String sql = "INSERT INTO news VALUES (default, ?, ?, ?)";
        jdbc.update(sql, news.getTitle(), news.getContent(), news.getDate());
    }

    public void createStandardNews() {
        addNews(new News("Welcome to Oddssey!",
                "Welcome to Oddssey, your go-to platform for sports predictions and news! " +
                        "Stay tuned for exciting updates and features.",
                LocalDate.now()));
    }
}
