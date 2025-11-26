package com.crime;

public class Crime {
    private int id;
    private String crimeType, location, dateReported, suspectName, status, description;

    public Crime(int id, String crimeType, String location, String dateReported,
                 String suspectName, String status, String description) {
        this.id = id; this.crimeType = crimeType; this.location = location;
        this.dateReported = dateReported; this.suspectName = suspectName;
        this.status = status; this.description = description != null ? description : "";
    }

    public int getId() { return id; }
    public String getCrimeType() { return crimeType; }
    public String getLocation() { return location; }
    public String getDateReported() { return dateReported; }
    public String getSuspectName() { return suspectName; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
}
