package bssm.plantshuman.peopleandgreen.recommendation.application.service;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.RecommendationPolicy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "recommendation.policy")
public class RecommendationPolicyProperties {

    private int sunlightExactScore = 30;
    private int sunlightAdjacentScore = 15;
    private int ventilationExactScore = 10;
    private int ventilationAdjacentScore = 5;
    private int temperatureExactScore = 20;
    private int temperatureSoftMargin = 3;
    private int temperatureHardMargin = 7;
    private int humidityExactScore = 15;
    private int humiditySoftMargin = 10;
    private int humidityHardMargin = 20;
    private int petCautionPenalty = 18;
    private int petToxicPenalty = 100;

    public RecommendationPolicy toPolicy() {
        return new RecommendationPolicy(
                sunlightExactScore,
                sunlightAdjacentScore,
                ventilationExactScore,
                ventilationAdjacentScore,
                temperatureExactScore,
                temperatureSoftMargin,
                temperatureHardMargin,
                humidityExactScore,
                humiditySoftMargin,
                humidityHardMargin,
                petCautionPenalty,
                petToxicPenalty
        );
    }

    public int getSunlightExactScore() {
        return sunlightExactScore;
    }

    public void setSunlightExactScore(int sunlightExactScore) {
        this.sunlightExactScore = sunlightExactScore;
    }

    public int getSunlightAdjacentScore() {
        return sunlightAdjacentScore;
    }

    public void setSunlightAdjacentScore(int sunlightAdjacentScore) {
        this.sunlightAdjacentScore = sunlightAdjacentScore;
    }

    public int getVentilationExactScore() {
        return ventilationExactScore;
    }

    public void setVentilationExactScore(int ventilationExactScore) {
        this.ventilationExactScore = ventilationExactScore;
    }

    public int getVentilationAdjacentScore() {
        return ventilationAdjacentScore;
    }

    public void setVentilationAdjacentScore(int ventilationAdjacentScore) {
        this.ventilationAdjacentScore = ventilationAdjacentScore;
    }

    public int getTemperatureExactScore() {
        return temperatureExactScore;
    }

    public void setTemperatureExactScore(int temperatureExactScore) {
        this.temperatureExactScore = temperatureExactScore;
    }

    public int getTemperatureSoftMargin() {
        return temperatureSoftMargin;
    }

    public void setTemperatureSoftMargin(int temperatureSoftMargin) {
        this.temperatureSoftMargin = temperatureSoftMargin;
    }

    public int getTemperatureHardMargin() {
        return temperatureHardMargin;
    }

    public void setTemperatureHardMargin(int temperatureHardMargin) {
        this.temperatureHardMargin = temperatureHardMargin;
    }

    public int getHumidityExactScore() {
        return humidityExactScore;
    }

    public void setHumidityExactScore(int humidityExactScore) {
        this.humidityExactScore = humidityExactScore;
    }

    public int getHumiditySoftMargin() {
        return humiditySoftMargin;
    }

    public void setHumiditySoftMargin(int humiditySoftMargin) {
        this.humiditySoftMargin = humiditySoftMargin;
    }

    public int getHumidityHardMargin() {
        return humidityHardMargin;
    }

    public void setHumidityHardMargin(int humidityHardMargin) {
        this.humidityHardMargin = humidityHardMargin;
    }

    public int getPetCautionPenalty() {
        return petCautionPenalty;
    }

    public void setPetCautionPenalty(int petCautionPenalty) {
        this.petCautionPenalty = petCautionPenalty;
    }

    public int getPetToxicPenalty() {
        return petToxicPenalty;
    }

    public void setPetToxicPenalty(int petToxicPenalty) {
        this.petToxicPenalty = petToxicPenalty;
    }
}
