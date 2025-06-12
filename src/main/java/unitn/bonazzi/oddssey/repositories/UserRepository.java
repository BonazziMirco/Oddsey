package unitn.bonazzi.oddssey.repositories;

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

    public User userData(String username) {
        String sql =    "SELECT * FROM userdata " +
                        "JOIN authorities ON userdata.id = authorities.id " +
                        "JOIN giornate ON giornate.id= userdata.id " +
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
            user.setPlays(resultSet.getInt("WAGERS"));
            user.setWins(resultSet.getInt("WINS"));

            Blob propicBlob = resultSet.getBlob("PROPIC");
            if (propicBlob != null) {
                user.setPropic(propicBlob.getBytes(1, (int) propicBlob.length()));
            }

            return user;
        };
        User user = jdbc.queryForObject(sql,userRowMapper, username);


        sql =    "SELECT wins FROM userdata " +
                "JOIN giornate ON giornate.id= userdata.id " +
                "WHERE USERNAME = ? ";
        RowMapper<Integer> winsRowMapper = (resultSet, rowNum) -> {
            return resultSet.getInt("WINS");
        };
        int vittorie = jdbc.queryForObject(sql,winsRowMapper, username);


        sql =    "SELECT COUNT(id) AS ranking FROM giornate WHERE wins > ? ";
        winsRowMapper = (resultSet, rowNum) -> {
            return resultSet.getInt("ranking");
        };
        user.setRank(jdbc.queryForObject(sql,winsRowMapper, vittorie) + 1);

        sql =   "SELECT COUNT(name) AS n_premi FROM PREMI WHERE winner = ? ";
        winsRowMapper = (resultSet, rowNum) -> {
            return resultSet.getInt("n_premi");
        };
        user.setPrizes(jdbc.queryForObject(sql,winsRowMapper, username));

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

    public List<User> getRankinList(){
        String sql = "SELECT id FROM giornate ORDER BY wins DESC";
        RowMapper<String> idRowMapper = (r, i) -> r.getString("id");
        List<Integer> ids = jdbc.queryForList(sql, Integer.class);
        List<String> usernames = new ArrayList<>();
        for (Integer id : ids) {
            usernames.add(this.getUsername(id));
        }
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
        sql =    "INSERT INTO giornate VALUES (DEFAULT, 0, 0)";
        jdbc.update(sql);
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
//
//    public int getUserId(String username) {
//        String sql = "SELECT id FROM userdata WHERE username = ?";
//        RowMapper<Integer> idRowMapper = (r, i) -> r.getInt("id");
//        return jdbc.queryForObject(sql, idRowMapper, username);
//    }
}