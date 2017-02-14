package com.example.orjaneide.bridgeclub.util;

import android.util.Xml;
import com.example.orjaneide.bridgeclub.model.Club;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ClubXmlParser {
    private static final String ns = null;
    private static final Club.ClubBuilder builder = new Club.ClubBuilder();

    public List<Club> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readXML(parser);
        } finally {
            in.close();
        }
    }

    private List<Club> readXML(XmlPullParser parser) throws  XmlPullParserException,
            IOException {
        List<Club> clubs = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "bridge");
        while(parser.next() != XmlPullParser.END_TAG) {
            if(parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            // Get tag name
            String tagName = parser.getName();

            // Look for club-tags, skip others
            if(tagName.equalsIgnoreCase("club")) {
                clubs.add(readClub(parser));
            } else {
                skip(parser);
            }
        }

        return clubs;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private Club readClub(XmlPullParser parser) throws  XmlPullParserException,
            IOException  {
        parser.require(XmlPullParser.START_TAG, ns, "club");

        while(parser.next() != XmlPullParser.END_TAG ) {
            if(parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String tag = parser.getName();
            if(tag.equalsIgnoreCase("name")) {
                builder.withName(readValue(parser, "name"));
            } else if(tag.equalsIgnoreCase("place")) {
                builder.withPlace(readValue(parser, "place"));
            } else if(tag.equalsIgnoreCase("address")) {
                builder.withAddress(readValue(parser, "address"));
            } else if(tag.equalsIgnoreCase("contactperson")) {
                builder.withContactPerson(readValue(parser, "contactperson"));
            } else if(tag.equalsIgnoreCase("gametimes")) {
                skip(parser);
            } else if(tag.equalsIgnoreCase("webpage")) {
                builder.withWebPage(readValue(parser, "webpage"));
            } else if(tag.equalsIgnoreCase("email")) {
                builder.withEmail(readValue(parser, "email"));
            } else if(tag.equalsIgnoreCase("phone")) {
                builder.withPhone(readValue(parser, "phone"));
            }
        }

        return builder.build();
    }

    private String readValue(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String res = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return res;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if(parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }

        return result;
    }
}
