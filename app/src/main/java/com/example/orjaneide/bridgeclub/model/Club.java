package com.example.orjaneide.bridgeclub.model;

import java.util.List;

public class Club {
    private int clubNumber;
    private String name;
    private String place;
    private String address;
    private String contactPerson;
    private List<String> playTimes;
    private String webPage;
    private String email;
    private String phone;

    public Club(int clubNumber, String place, String address, String contactPerson, String webPage, String email, String phone) {
        this.clubNumber = clubNumber;
        this.place = place;
        this.address = address;
        this.contactPerson = contactPerson;
        this.webPage = webPage;
        this.email = email;
        this.phone = phone;
    }

    public int getClubNumber() {
        return clubNumber;
    }

    public String getName() {
        return name;
    }

    public String getPlace() {
        return place;
    }

    public String getAddress() {
        return address;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public List<String> getPlayTimes() {
        return playTimes;
    }

    public String getWebPage() {
        return webPage;
    }


    public String getEmail() {
        return email;
    }


    public String getPhone() {
        return phone;
    }

    public Club(ClubBuilder builder) {
        this.clubNumber = builder.clubNumber;
        this.address = builder.address;
        this.email = builder.email;
        this.contactPerson = builder.contactPerson;
        this.place = builder.place;
        this.webPage = builder.webPage;
        this.phone = builder.phone;
        this.playTimes = builder.playTimes;
        this.name = builder.name;
    }

    public static class ClubBuilder {
        private int clubNumber;
        private String place;
        private String address;
        private String contactPerson;
        private List<String> playTimes;
        private String webPage;
        private String email;
        private String phone;
        private String name;

        public ClubBuilder withClubNumber(int clubNumber) {
            this.clubNumber = clubNumber;
            return this;
        }

        public ClubBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public ClubBuilder withPlace(String place) {
            this.place = place;
            return this;
        }

        public ClubBuilder withAddress(String address) {
            this.address = address;
            return this;
        }

        public ClubBuilder withContactPerson(String contactPerson) {
            this.contactPerson = contactPerson;
            return this;
        }

        public ClubBuilder withWebPage(String webPage) {
            this.webPage = webPage;
            return this;
        }

        public ClubBuilder withPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public ClubBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public Club build() {
            return new Club(this);
        }
    }
}
