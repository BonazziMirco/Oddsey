package unitn.bonazzi.oddssey.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public class PlaysRepository {
    private final JdbcTemplate jdbc;

    public PlaysRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void addPlay(String username, int points) {
        String sql = "INSERT INTO giornate (username, points) VALUES (?, ?)";
        jdbc.update(sql, username, points);
    }

    public boolean userPlayed(String username) {
        String sql = "SELECT COUNT(*) FROM giornate WHERE username=? AND dateOfPlay = CURRENT_DATE";
        int count = jdbc.queryForObject(sql, Integer.class, username);
        return count > 0;
    }

    public int getTodaysPoints(String username) {
        String sql = "SELECT points FROM giornate WHERE username=? AND dateOfPlay = ?";
        return jdbc.queryForObject(sql, Integer.class, username, LocalDate.now());
    }
}
