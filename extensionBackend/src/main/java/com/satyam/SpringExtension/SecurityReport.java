package com.satyam.SpringExtension;

import java.util.List;

public class SecurityReport {
    private boolean https;
    private boolean hasCsp;
    private int score;
    private List<String> warnings;

    // Getters and setters
    public boolean isHttps() {
        return https;
    }

    public void setHttps(boolean https) {
        this.https = https;
    }

    public boolean isHasCsp() {
        return hasCsp;
    }

    public void setHasCsp(boolean hasCsp) {
        this.hasCsp = hasCsp;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }
}
