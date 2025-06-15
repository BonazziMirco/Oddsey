package unitn.bonazzi.oddssey.pojos;

import java.util.Date;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private Date birthdate;
    private String email;
    private String username;
    private String password;
    private String sport;
    private String team;
    private int plays;
    private int points;
    private int prizes;
    private int rank;
    private String role;
    private byte[] propic;

    public User() {
        this.id = 0;
        this.firstName = "";
        this.lastName = "";
        this.birthdate = null;
        this.email = "";
        this.username = "";
        this.password = "";
        this.sport = "";
        this.team = "";
        this.plays = 0;
        this.points = 0;
        this.prizes = 0;
        this.rank = 0;
        this.role = "";
        this.propic = null;
    }

    public User(String firstName, String lastName, Date birthdate, String email, String username, String password, String sport, String team, String role, byte[] propic) {
        this.id = 0;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.email = email;
        this.username = username;
        this.password = password;
        this.sport = sport;
        this.team = team;
        this.plays = 0;
        this.points = 0;
        this.prizes = 0;
        this.rank = 0;
        this.role = role;
        this.propic = propic;
    }

    public int getId() {return id;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public Date getBirthdate() {return birthdate;}
    public String getEmail() {return email;}
    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public String getSport() {return sport;}
    public String getTeam() {return team;}
    public int getPlays() {return plays;}
    public int getPoints() {return points;}
    public int getPrizes() {return prizes;}
    public int getRank() {return rank;}
    public String getRole() {return role;}
    public byte[] getPropic() {return propic;}

    public void setId(int id) {this.id = id;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setBirthdate(Date birthdate) {this.birthdate = birthdate;}
    public void setEmail(String email) {this.email = email;}
    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
    public void setSport(String sport) {this.sport = sport;}
    public void setTeam(String team) {this.team = team;}
    public void setPlays(int plays) {this.plays = plays;}
    public void setPoints(int points) {this.points = points;}
    public void setPrizes(int prizes) {this.prizes = prizes;}
    public void setRank(int rank) {this.rank = rank;}
    public void setRole(String role) {this.role = role;}
    public void setPropic(byte[] propic) {this.propic = propic;}
}
