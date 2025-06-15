package unitn.bonazzi.oddssey.repositories;

import ch.qos.logback.core.joran.sanity.Pair;
import unitn.bonazzi.oddssey.pojos.SecurityUser;
import unitn.bonazzi.oddssey.pojos.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Repository;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbc;
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public UserRepository(JdbcTemplate jdbc,
                          UserDetailsManager userDetailsManager,
                          PasswordEncoder passwordEncoder) {
        this.jdbc = jdbc;
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean userExists(String username) {
        return userDetailsManager.userExists(username);
    }

    public List<User>  getRankingList(){
        String sql = "SELECT username, SUM(points) as tot_punti  FROM giornate GROUP BY username ORDER BY points DESC";

        RowMapper<User> rankingRowMapper = (r, i) -> {
            User user = new User();
            user.setUsername(r.getString("username"));
            user.setPoints(r.getInt("tot_punti"));
            user.setRank(i + 1);
            return user;
        };
        return jdbc.query(sql, rankingRowMapper);
    }

    public User userData(String username) {
        String sql =    "SELECT * FROM userdata " +
                        "JOIN authorities ON userdata.id = authorities.id " +
                        "WHERE userdata.USERNAME = ?";
        RowMapper<User> userRowMapper = (resultSet, rowNum) -> {
            User user = new User();
            user.setId(resultSet.getInt("ID"));
            user.setFirstName(resultSet.getString("FIRSTNAME"));
            user.setLastName(resultSet.getString("LASTNAME"));
            user.setBirthdate(resultSet.getDate("BIRTHDATE"));
            user.setEmail(resultSet.getString("EMAIL"));
            user.setUsername(resultSet.getString("USERNAME"));
            user.setRole(resultSet.getString("AUTHORITY"));
            user.setSport(resultSet.getString("SPORT"));
            user.setTeam(resultSet.getString("TEAM"));

            Blob propicBlob = resultSet.getBlob("PROPIC");
            if (propicBlob != null) {
                user.setPropic(propicBlob.getBytes(1, (int) propicBlob.length()));
            }

            return user;
        };
        User user = jdbc.queryForObject(sql,userRowMapper, username);

        sql =   "SELECT COUNT(name) AS n_premi FROM PREMI WHERE winner = ? ";
        RowMapper<Integer> intRowMapper = (resultSet, rowNum) -> resultSet.getInt("n_premi");
        user.setPrizes(jdbc.queryForObject(sql,intRowMapper, username));

        sql =   "SELECT COUNT(*) AS n_giornate FROM giornate WHERE USERNAME = ?";
        intRowMapper = (resultSet, rowNum) -> resultSet.getInt("n_giornate");
        user.setPlays(jdbc.queryForObject(sql, intRowMapper, username));

        sql =   "SELECT COUNT(*) AS n_giornate FROM giornate WHERE USERNAME = ?";
        intRowMapper = (resultSet, rowNum) -> resultSet.getInt("n_giornate");
        user.setPlays(jdbc.queryForObject(sql, intRowMapper, username));

        List<User> rankingList = this.getRankingList();
        for (int i = 0; i < rankingList.size(); i++) {
            if(rankingList.get(i).getUsername().equals(username)) {
                user.setRank(i + 1);
                user.setPoints(rankingList.get(i).getPoints());
                break;
            }
        }

        return user;
    }

    public String getUsername(int id) {
        String sql = "SELECT username FROM userdata WHERE id = ?";
        RowMapper<String> usernameRowMapper = (r, i) -> r.getString("username");
        return jdbc.queryForObject(sql, usernameRowMapper, id);
    }

    public List<User> findAllUsers() {
        String sql = "SELECT username FROM userdata";
        RowMapper<String> usernameRowMapper = (r, i) -> r.getString("username");
        List<String> usernames = jdbc.query(sql, usernameRowMapper);
        List<User> users = new ArrayList<>();
        for (String username : usernames) {
            users.add(this.userData(username));
        }
        return users;
    }

    public void addUser(User user) {
        String sql =    "INSERT INTO userdata VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbc.update(sql,
                user.getFirstName(),
                user.getLastName(),
                user.getBirthdate(),
                user.getEmail(),
                user.getUsername(),
                user.getSport(),
                user.getTeam(),
                user.getPropic()
        );
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDetailsManager.createUser(new SecurityUser(user));
    }

    public String getUserSport(String username) {
        String sql = "SELECT sport FROM userdata WHERE username = ?";
        RowMapper<String> sportRowMapper = (r, i) -> r.getString("sport");
        return jdbc.queryForObject(sql, sportRowMapper, username);
    }

    public List<User> getUsersOnly(){
        String sql = "SELECT * FROM AUTHORITIES " +
                     "JOIN userdata ON authorities.id = userdata.id " +
                     "WHERE authority = 'ROLE_USER'";
        RowMapper<User> userRowMapper = (resultSet, rowNum) -> {
            User user = new User();
            user.setId(resultSet.getInt("ID"));
            user.setFirstName(resultSet.getString("FIRSTNAME"));
            user.setLastName(resultSet.getString("LASTNAME"));
            user.setBirthdate(resultSet.getDate("BIRTHDATE"));
            user.setEmail(resultSet.getString("EMAIL"));
            user.setUsername(resultSet.getString("USERNAME"));

            return user;
        };
        return jdbc.query(sql, userRowMapper);
    }

    public void promoteUser(int id) {
        String sql = "UPDATE authorities SET authority = 'ROLE_MODERATOR' WHERE id = ?";
        jdbc.update(sql, id);
    }

    public boolean changePassword(String username, String newPassword, String oldPassword) {
        String sql = "SELECT password FROM users WHERE username = ?";
        RowMapper<String> passwordRowMapper = (r, i) -> r.getString("password");
        String currentPassword = jdbc.queryForObject(sql, passwordRowMapper, username);
        boolean result = passwordEncoder.matches(oldPassword, currentPassword);
        if (result) {
            userDetailsManager.changePassword(passwordEncoder.encode(oldPassword), passwordEncoder.encode(newPassword));
        }
        return result;
    }
}