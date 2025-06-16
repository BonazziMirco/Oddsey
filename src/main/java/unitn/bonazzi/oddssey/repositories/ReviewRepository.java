package unitn.bonazzi.oddssey.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import unitn.bonazzi.oddssey.pojos.Review;

import java.time.LocalDate;
import java.util.List;

@Repository
public class ReviewRepository {
    private final JdbcTemplate jdbc;

    public ReviewRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Review> getAllReviews(){
        String sql = "SELECT * FROM recensioni";
        RowMapper<Review> rowMapper = (r, i) -> {
            Review rowObject = new Review();
            rowObject.setDate(r.getDate("PUBLISHINGDATE").toLocalDate());
            rowObject.setRating(r.getInt("RATING"));
            rowObject.setAuthor(r.getString("AUTHOR"));
            rowObject.setText(r.getString("TEXT"));
            return rowObject;
        };
        return jdbc.query(sql, rowMapper);
    }

    public void addReview(Review review) {
        String sql = "INSERT INTO RECENSIONI VALUES (?,?,?,?)";
        jdbc.update(sql, review.getAuthor(), review.getText(), review.getRating(), LocalDate.now());
    }

    public int getAverageRating() {
        String sql = "SELECT AVG(RATING) FROM RECENSIONI";
        return jdbc.queryForObject(sql, Integer.class);
    }

    public Review getUserReview(String author) {
        String sql = "SELECT * FROM RECENSIONI WHERE AUTHOR=?";
        RowMapper<Review> rowMapper = (r, i) -> {
            Review rowObject = new Review();
            rowObject.setRating(r.getInt("RATING"));
            rowObject.setText(r.getString("TEXT"));
            rowObject.setDate(r.getDate("PUBLISHINGDATE").toLocalDate());
            return rowObject;
        };
        return jdbc.queryForObject(sql, rowMapper, author);
    }

    public boolean userReviewed(String author) {
        String sql = "SELECT COUNT(*) FROM RECENSIONI WHERE AUTHOR=?";
        int count = jdbc.queryForObject(sql, Integer.class, author);
        return count > 0;
    }

    public void updateReview(Review review) {
        String sql = "UPDATE RECENSIONI SET TEXT=?, RATING=?, PUBLISHINGDATE=? WHERE AUTHOR=?";
        jdbc.update(sql, review.getText(), review.getRating(), LocalDate.now(), review.getAuthor());
    }
}
