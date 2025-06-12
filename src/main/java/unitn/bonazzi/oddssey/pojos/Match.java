package unitn.bonazzi.oddssey.pojos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Match {
    private int id;
    private LocalDate matchDate;
    private String homeTeam;
    private String awayTeam;
    private String Sport;
    private int matchResult;

    public Match() {
        this.id=0;
        this.matchDate = null;
        this.homeTeam="";
        this.awayTeam="";
        this.Sport="";
        this.matchResult=3;
    }

    public Match(String homeTeam, String awayTeam, String sport) {
        this.id=0;
        this.matchDate = LocalDate.now();
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.Sport = sport;

        Random random = new Random();
        this.matchResult= random.nextInt(3);
    }

    public Match(int id, LocalDate matchDate, String homeTeam, String awayTeam, String sport, int matchResult) {
        this.id = id;
        this.matchDate = matchDate;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.Sport = sport;
        this.matchResult = matchResult;
    }

    public int getMatchResult() {
        return matchResult;
    }

    public void setMatchResult(int matchResult) {
        this.matchResult = matchResult;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSport() {
        return Sport;
    }

    public void setSport(String sport) {
        Sport = sport;
    }

    public LocalDate getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDate matchDate) {
        this.matchDate = matchDate;
    }

    @Override
    public String toString() {
        return "Match{id=" + id + ", teamA='" + homeTeam + "', teamB='" + awayTeam + "', date=" + matchDate + "}";
    }
}
