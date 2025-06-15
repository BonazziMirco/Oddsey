package unitn.bonazzi.oddssey.requestBodies;

import java.util.List;

public class WagerRequest {
    List<Integer> bets;

    public WagerRequest() {
    }

    public WagerRequest(List<Integer> bets) {
        this.bets = bets;
    }

    public List<Integer> getBets() {
        return bets;
    }

    public void setBets(List<Integer> bets) {
        this.bets = bets;
    }
}
