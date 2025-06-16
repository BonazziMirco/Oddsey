package unitn.bonazzi.oddssey.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PrizeRepository {
    private final JdbcTemplate jdbc;

    public PrizeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void createPrize(String winner, String name){
        String sql = "INSERT INTO premi (name, winner) VALUES (?, ?)";
        jdbc.update(sql, name, winner);
    }

    public int getPrizeCount(String winner){
        String sql =   "SELECT COUNT(name) AS n_premi FROM PREMI WHERE winner = ? ";
        RowMapper<Integer> intRowMapper = (resultSet, rowNum) -> resultSet.getInt("n_premi");
        return jdbc.queryForObject(sql,intRowMapper, winner);
    }
}
