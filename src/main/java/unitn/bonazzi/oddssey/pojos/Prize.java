package unitn.bonazzi.oddssey.pojos;

public class Prize {
    private String name;
    private String winner;

    public Prize(String name, String winner) {
        this.name = name;
        this.winner = winner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}
