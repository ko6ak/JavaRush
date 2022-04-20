package com.game.service;

import com.game.entity.Player;

import java.util.List;
import java.util.Map;

public interface PlayerService {
    List<Player> getAllPlayers(Map<String, String> allParams);
    void createOrUpdatePlayer(Player player);
    Player getPlayer(long id);
    void deletePlayer(long id);
    long getCount(Map<String, String> allParams);
}
