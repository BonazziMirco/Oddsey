package unitn.bonazzi.oddssey.requestBodies;

import java.util.List;

public class WagerRequest {
    List<Integer> predictions;

    public WagerRequest() {
    }

    public WagerRequest(List<Integer> predictions) {
        this.predictions = predictions;
    }

    public List<Integer> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<Integer> predictions) {
        this.predictions = predictions;
    }
}
