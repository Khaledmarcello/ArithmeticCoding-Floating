public class Probability {

    String text;
    int count;
    double lower;
    double upper;
    double probability;

    public Probability() {
    }

    public Probability(String t, int c, double lo, double hi, double prob) {
        text = t;
        count = c;
        lower = lo;
        upper = hi;
        probability = prob;
    }


}
