package entity;

public class SentimentResultFactory {
    public SentimentResult create(String sentimentWord, String sentimentExplanation) {
        return new SentimentResult(sentimentWord, sentimentExplanation);
    }
}
