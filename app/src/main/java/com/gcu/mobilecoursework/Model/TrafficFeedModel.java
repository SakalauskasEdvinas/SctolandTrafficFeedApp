package com.gcu.mobilecoursework.model;

import com.gcu.mobilecoursework.helper.DateUtility;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

// By Edvinas Sakalauskas - S1627176
public class TrafficFeedModel implements Serializable {
    private String title;
    private String description;
    private String link;
    private String geoPoint;
    private String author;
    private String comments;

    private Date startDate;
    private Date endDate;
    private Date pubDate;

    private RoadworkType roadworkType;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.parseStartAndEndDatesFromDescription(description);
        if (roadworkType == RoadworkType.PLANNED_ROADWORK) {
            parsePlannedRoadworkDescriptionValues();
        }


    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(String geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(String nextText) {
        this.pubDate = DateUtility.stringToDate(nextText);

    }

    private void parsePlannedRoadworkDescriptionValues() {
        String[] parts = this.description.split("(?=Type :|Location :|Lane Closures :|Works :|Traffic Management :|Status :)");
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : parts) {
            stringBuilder.append(string.replaceAll(" :", ":"));
            stringBuilder.append("\n \n");
        }
        this.description = stringBuilder.toString();
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    protected void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStartDateString() {
        return DateUtility.dateToString(startDate);
    }

    public String getEndDateString() {
        return DateUtility.dateToString(endDate);
    }

    public String getPubDateString() {
        return DateUtility.dateToString(pubDate);
    }

    public RoadworkType getRoadworkType() {
        return roadworkType;
    }


    private void parseStartAndEndDatesFromDescription(String description) {
        String[] stringParts = description.split("<br />");


        if (stringParts.length >= 2) {
            String startDateString = stringParts[0].split("Start Date:")[1];
            String endDateString = stringParts[1].split("End Date:")[1];

            this.startDate = DateUtility.stringToDate(startDateString);
            this.endDate = DateUtility.stringToDate(endDateString);
        }

        //if descriptions has more than the dates, set the remaining value  as the new description
        if (stringParts.length >= 3) {
            this.description = stringParts[2];
        } else {
            this.description = "No description provided";
        }


    }

    public Double[] getCoordinates() {
        String[] parts = this.geoPoint.split(" ", -1);
        Double[] coordinates = new Double[2];
        coordinates[0] = Double.parseDouble(parts[0]);
        coordinates[1] = Double.parseDouble(parts[1]);
        return coordinates;
    }

    public void setType(RoadworkType roadworkType) {
        this.roadworkType = roadworkType;
    }

    public boolean checkIfIsDuringADate(Date targetDate) {
        if (startDate == null) {
            return true;
        }
        return (targetDate.after(startDate) || targetDate.equals(startDate)) && (targetDate.before(endDate) || targetDate.equals(endDate));
    }

    public boolean matchesTitleOrDescription(String query) {
        return this.getTitle() != null && this.getTitle().toLowerCase().contains(query.toLowerCase())
                || this.getDescription() != null && this.getDescription().toLowerCase().contains(query.toLowerCase());

    }

    public long getRoadworkDayLength() {
        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
        long diff = 0;

        try {
            Date date1 = myFormat.parse(DateUtility.dateToString(startDate));
            Date date2 = myFormat.parse(DateUtility.dateToString(endDate));
            diff = date2.getTime() - date1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public String toString() {
        return "TrafficFeedModel{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                ", geoPoint='" + geoPoint + '\'' +
                ", author='" + author + '\'' +
                ", comments='" + comments + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", pubDate=" + pubDate +
                ", roadworkType=" + roadworkType +
                '}';
    }
}
