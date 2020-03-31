package com.gcu.mobilecoursework.Model;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class TrafficFeedModel {
    private String title;
    private String description;
    private String link;
    private String geoPoint;
    private String author;
    private String comments;

    private Date startDate;
    private Date endDate;


    private Date pubDate;

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


    public void setPubDate(String nextText) {
        System.out.println("Parsing date");
        this.pubDate = stringToDate(nextText);

    }


    public Date getPubDate() {
        return pubDate;
    }

    public void parseStartAndEndDatesFromDescription(String description) {
//        description = description.replaceAll("<.*?>","\n");
        DateFormat format = new SimpleDateFormat("EE, dd MMMMM y hh:mm:ss z", Locale.ENGLISH);
        String[] stringParts = description.split("<br />");

        String startDate = stringParts[0].split("Start Date:")[1];
        String endDate =stringParts[1].split("End Date:")[1];
        String descriptionWithoutDates = stringParts[2];
        System.out.println("////////////////////////////////////////////////////////////////////////////////////////////////////");
        System.out.println(startDate);
        System.out.println("//////////////////////////////////////////////////////////////////////////////////////////////////////");
        System.out.println(endDate);
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

        this.startDate = stringToDate(startDate);
        this.endDate = stringToDate(endDate);
        System.out.println(descriptionWithoutDates);
        this.description = descriptionWithoutDates;
    }


    private Date stringToDate(String dateString) {
        DateFormat format = new SimpleDateFormat("EE, dd MMMMM y hh:mm:ss z", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            Log.e("Date parse failed", Objects.requireNonNull(e.getLocalizedMessage()));
        }

        return date;
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
                ", pubDate=" + pubDate +
                '}';
    }

}
