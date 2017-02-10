package com.example.orjaneide.bridgeclub.util;

import com.example.orjaneide.bridgeclub.model.Club;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class UserHandler extends DefaultHandler {
    // Fields
    boolean bName;
    boolean bPlace;
    boolean bAddress;
    boolean bContactPerson;
    boolean bWebPage;
    boolean bEmail;
    boolean bPhone;
    boolean bGameTimes;

    // Objects to help builing Club-objects
    Club.ClubBuilder mBuilder = new Club.ClubBuilder();
    public List<Club> mClubs = new ArrayList<>();

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("name")) {
            bName = true;
        } else if (qName.equalsIgnoreCase("place")) {
            bPlace = true;
        } else if (qName.equalsIgnoreCase("address")) {
            bAddress = true;
        } else if (qName.equalsIgnoreCase("contactperson")) {
            bContactPerson = true;
        } else if (qName.equalsIgnoreCase("webpage")) {
            bWebPage = true;
        } else if (qName.equalsIgnoreCase("email")) {
            bEmail = true;
        } else if (qName.equalsIgnoreCase("phone")) {
            bPhone = true;
        } else if (qName.equalsIgnoreCase("gametimes")) {
            bGameTimes = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        mClubs.add(mBuilder.build());
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if(bName) {
            mBuilder.withName(new String(ch, start, length));
            bName = false;
        } else if(bPlace) {
            mBuilder.withPlace(new String(ch, start, length));
            bPlace = false;
        } else if(bAddress) {
            mBuilder.withAddress(new String(ch, start, length));
            bAddress = false;
        } else if(bContactPerson) {
            mBuilder.withContactPerson(new String(ch, start, length));
            bContactPerson = false;
        } else if(bWebPage) {
            mBuilder.withWebPage(new String(ch, start, length));
            bWebPage = false;
        } else if(bEmail) {
            mBuilder.withEmail(new String(ch, start, length));
            bEmail = false;
        } else if(bPhone) {
            mBuilder.withPhone(new String(ch, start, length));
            bPhone = false;
        }
    }
}
