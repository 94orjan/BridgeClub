package com.example.orjaneide.bridgeclub.model;

import java.util.List;

public interface ClubDao {
    Club findClubById(int clubNumber);
    List<Club> getAllClubs();
}
