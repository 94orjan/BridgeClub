package com.example.orjaneide.bridgeclub.model;


import java.util.List;

public class ClubDaoImpl implements ClubDao {
    @Override
    public Club findClubById(int clubNumber) {
        List<Club> clubs = getAllClubs();
        for(Club club : clubs) {
            if(club.getClubNumber() == clubNumber) {
                return club;
            }
        }

        return null;
    }

    @Override
    public List<Club> getAllClubs() {
        return Club.getClubs();
    }
}
