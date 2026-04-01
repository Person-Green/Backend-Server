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
    private int lowCareLongCycleDays = 14;
    private int lowCareMediumCycleDays = 7;
    private int lowCareLongCycleScore = 5;
    private int lowCareMediumCycleScore = 3;
    private int lowCareShortCycleScore = 1;
    private int mediumCareMinCycleDays = 7;
    private int mediumCareMaxCycleDays = 14;
    private int mediumCareIdealScore = 5;
    private int mediumCareLongCycleScore = 4;
    private int mediumCareShortCycleScore = 3;
    private int highCareShortCycleDays = 7;
    private int highCareMediumCycleDays = 14;
    private int highCareShortCycleScore = 5;
    private int highCareMediumCycleScore = 4;
    private int highCareLongCycleScore = 3;
    private int petCautionPenalty = 18;
    private int petToxicPenalty = 100;
    private int maxRecommendations = 10;

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
                lowCareLongCycleDays,
                lowCareMediumCycleDays,
                lowCareLongCycleScore,
                lowCareMediumCycleScore,
                lowCareShortCycleScore,
                mediumCareMinCycleDays,
                mediumCareMaxCycleDays,
                mediumCareIdealScore,
                mediumCareLongCycleScore,
                mediumCareShortCycleScore,
                highCareShortCycleDays,
                highCareMediumCycleDays,
                highCareShortCycleScore,
                highCareMediumCycleScore,
                highCareLongCycleScore,
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

    public int getLowCareLongCycleDays() {
        return lowCareLongCycleDays;
    }

    public void setLowCareLongCycleDays(int lowCareLongCycleDays) {
        this.lowCareLongCycleDays = lowCareLongCycleDays;
    }

    public int getLowCareMediumCycleDays() {
        return lowCareMediumCycleDays;
    }

    public void setLowCareMediumCycleDays(int lowCareMediumCycleDays) {
        this.lowCareMediumCycleDays = lowCareMediumCycleDays;
    }

    public int getLowCareLongCycleScore() {
        return lowCareLongCycleScore;
    }

    public void setLowCareLongCycleScore(int lowCareLongCycleScore) {
        this.lowCareLongCycleScore = lowCareLongCycleScore;
    }

    public int getLowCareMediumCycleScore() {
        return lowCareMediumCycleScore;
    }

    public void setLowCareMediumCycleScore(int lowCareMediumCycleScore) {
        this.lowCareMediumCycleScore = lowCareMediumCycleScore;
    }

    public int getLowCareShortCycleScore() {
        return lowCareShortCycleScore;
    }

    public void setLowCareShortCycleScore(int lowCareShortCycleScore) {
        this.lowCareShortCycleScore = lowCareShortCycleScore;
    }

    public int getMediumCareMinCycleDays() {
        return mediumCareMinCycleDays;
    }

    public void setMediumCareMinCycleDays(int mediumCareMinCycleDays) {
        this.mediumCareMinCycleDays = mediumCareMinCycleDays;
    }

    public int getMediumCareMaxCycleDays() {
        return mediumCareMaxCycleDays;
    }

    public void setMediumCareMaxCycleDays(int mediumCareMaxCycleDays) {
        this.mediumCareMaxCycleDays = mediumCareMaxCycleDays;
    }

    public int getMediumCareIdealScore() {
        return mediumCareIdealScore;
    }

    public void setMediumCareIdealScore(int mediumCareIdealScore) {
        this.mediumCareIdealScore = mediumCareIdealScore;
    }

    public int getMediumCareLongCycleScore() {
        return mediumCareLongCycleScore;
    }

    public void setMediumCareLongCycleScore(int mediumCareLongCycleScore) {
        this.mediumCareLongCycleScore = mediumCareLongCycleScore;
    }

    public int getMediumCareShortCycleScore() {
        return mediumCareShortCycleScore;
    }

    public void setMediumCareShortCycleScore(int mediumCareShortCycleScore) {
        this.mediumCareShortCycleScore = mediumCareShortCycleScore;
    }

    public int getHighCareShortCycleDays() {
        return highCareShortCycleDays;
    }

    public void setHighCareShortCycleDays(int highCareShortCycleDays) {
        this.highCareShortCycleDays = highCareShortCycleDays;
    }

    public int getHighCareMediumCycleDays() {
        return highCareMediumCycleDays;
    }

    public void setHighCareMediumCycleDays(int highCareMediumCycleDays) {
        this.highCareMediumCycleDays = highCareMediumCycleDays;
    }

    public int getHighCareShortCycleScore() {
        return highCareShortCycleScore;
    }

    public void setHighCareShortCycleScore(int highCareShortCycleScore) {
        this.highCareShortCycleScore = highCareShortCycleScore;
    }

    public int getHighCareMediumCycleScore() {
        return highCareMediumCycleScore;
    }

    public void setHighCareMediumCycleScore(int highCareMediumCycleScore) {
        this.highCareMediumCycleScore = highCareMediumCycleScore;
    }

    public int getHighCareLongCycleScore() {
        return highCareLongCycleScore;
    }

    public void setHighCareLongCycleScore(int highCareLongCycleScore) {
        this.highCareLongCycleScore = highCareLongCycleScore;
    }

    public int getMaxRecommendations() {
        return maxRecommendations;
    }

    public void setMaxRecommendations(int maxRecommendations) {
        this.maxRecommendations = maxRecommendations;
    }
}
