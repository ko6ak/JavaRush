package com.game.service;

import com.game.controller.PlayerSpecification;
import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService{
    private PlayerRepository playerRepository;
    private PlayerSpecification playerSpecification;

    @Autowired
    public void setPlayerRepository(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
    @Autowired
    public void setPlayerSpecification(PlayerSpecification playerSpecification) {
        this.playerSpecification = playerSpecification;
    }

    @Override
    public List<Player> getAllPlayers(Map<String, String> allParams) {
        int pageNumber = 0;
        int pageSize = 3;
        try {
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                switch (entry.getKey()){
                    case "pageNumber":
                        pageNumber = Integer.parseInt(entry.getValue()); break;
                    case "pageSize":
                        pageSize = Integer.parseInt(entry.getValue()); break;
                }
            }
        }
        catch (NumberFormatException e){e.printStackTrace();}
        Pageable p = PageRequest.of(pageNumber, pageSize);

        Specification<Player> spec = playerSpecification.getPlayers(allParams);

        Page<Player> page;
        if (allParams.size() == 0) page = playerRepository.findAll(p);
        else page = playerRepository.findAll(spec, p);

        if (page != null) return page.getContent();
        return null;
    }

    @Override
    public void createOrUpdatePlayer(Player player) {
        playerRepository.save(player);
    }

    @Override
    public Player getPlayer(long id) {
        Player player = null;
        Optional<Player> optionalPlayer = playerRepository.findById(id);
        if (optionalPlayer.isPresent()) player = optionalPlayer.get();
        return player;
    }

    @Override
    public void deletePlayer(long id) {
        playerRepository.deleteById(id);
    }

    @Override
    public long getCount(Map<String, String> allParams) {
        Specification<Player> spec = playerSpecification.getPlayers(allParams);
        return playerRepository.count(spec);
    }
}
