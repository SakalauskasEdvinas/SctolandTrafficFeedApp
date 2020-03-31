package com.gcu.mobilecoursework.Helper;

import android.util.Log;

import com.gcu.mobilecoursework.Model.TrafficFeedModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class TrafficXMLParser {


    public List<TrafficFeedModel> parseXML(InputStream inputStream) throws IOException {
        List<TrafficFeedModel> trafficFeedModelList = null;
        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            trafficFeedModelList = parse(parser);

        } catch (XmlPullParserException e) {
           Log.i("Parser exception", Objects.requireNonNull(e.getMessage()));
        }

        return trafficFeedModelList;
    }


    private List<TrafficFeedModel> parse(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<TrafficFeedModel> incidents = new ArrayList<>();
        TrafficFeedModel trafficFeedModel = null;
        boolean done = false;


        int eventType = parser.getEventType();
        TrafficFeedModel item = null;
        while (eventType != XmlPullParser.END_DOCUMENT && !done) {
            String name = null;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("item")) {
                        Log.i("new item", "Create new item");
                        item = new TrafficFeedModel();
                    } else if (item != null) {
                        if (name.equalsIgnoreCase("title")) {
                            Log.i("Attribute", "setLink");
                            item.setTitle(parser.nextText());
                        } else if (name.equalsIgnoreCase("description")) {
                            Log.i("Attribute", "description");
                            item.setDescription(parser.nextText());
                        }else if (name.equalsIgnoreCase("link")) {
                            Log.i("Attribute", "link");
                            item.setLink(parser.nextText().trim());
                        } else if (name.equals("georss:point")) {
                            Log.i("Attribute", "georss");
                            item.setGeoPoint(parser.nextText().trim());
                        } else if (name.equals("author")) {
                            Log.i("Attribute", "author");
                            item.setAuthor(parser.nextText().trim());
                        } else if (name.equalsIgnoreCase("comments")) {
                            Log.i("Attribute", "comments");
                            item.setComments(parser.nextText());
                        } else if (name.equalsIgnoreCase("pubDate")) {
                            Log.i("Attribute", "date");
                            item.setPubDate(parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    Log.i("End tag", name);
                    if (name.equalsIgnoreCase("item") && item != null) {
                        Log.i("Added", item.toString());
                        incidents.add(item);
                    } else if (name.equalsIgnoreCase("channel")) {
                        done = true;
                    }
                    break;
            }
            eventType = parser.next();
        }
            return incidents;
        }





}
