package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.*;

import static java.util.Objects.isNull;

@RestController
@RequestMapping("/rest")
public class PlayerController {
    private final Field[] fields = Player.class.getDeclaredFields();
    private PlayerService playerService;

    @Autowired
    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    public ResponseEntity<List<Player>> showAllPlayers(@RequestParam Map<String, String> allParams){
        List<Player> players = playerService.getAllPlayers(allParams);
        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    @PostMapping("/players")
    public ResponseEntity<Player> createPlayer(@RequestBody Player player){
        for (Field f : fields){
            try {
                f.setAccessible(true);
                if (f.getName().equals("id")
                        || f.getName().equals("banned")
                        || f.getName().equals("level")
                        || f.getName().equals("untilNextLevel")) continue;
                if (isNull(f.get(player))) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (isNull(player.getBanned())) player.setBanned(false);
        if (!isValid(player)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        player.setId(null);
        playerService.createOrUpdatePlayer(calculateLevelAndRemainsForPlayer(player));
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @PostMapping("/players/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable String id, @RequestBody Player player){
        long longId;
        if ((longId = parseId(id)) <= 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Player p = playerService.getPlayer(longId);
        if (p == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        player.setId(longId);
        for (Field f : fields){
            try {
                f.setAccessible(true);
                if (f.getName().equals("id")
                        || f.getName().equals("level")
                        || f.getName().equals("untilNextLevel")) continue;
                if (isNull(f.get(player))) f.set(player, f.get(p));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (!isValid(player)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        playerService.createOrUpdatePlayer(calculateLevelAndRemainsForPlayer(player));
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @DeleteMapping("/players/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable String id){
        long longId;
        if ((longId = parseId(id)) <= 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Player p = playerService.getPlayer(longId);
        if (p == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        playerService.deletePlayer(longId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/players/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable String id){
        long longId;
        if ((longId = parseId(id)) <= 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Player p = playerService.getPlayer(longId);
        if (p == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(p, HttpStatus.OK);
    }

    private long parseId(String id){
        long l;
        try{
            l = Long.parseLong(id);
        }
        catch (NumberFormatException e) {
            return -1;
        }
        return l;
    }

    private Player calculateLevelAndRemainsForPlayer(Player player){
        int exp = player.getExperience();
        int level = (int) (Math.sqrt(2500 + 200 * exp) - 50) / 100;
        player.setLevel(level);
        player.setUntilNextLevel(50 * (level + 1) * (level + 2) - exp);
        return player;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isValid(Player player){
        String pl = player.getName();
        Integer plExp = player.getExperience();
        Date birth = player.getBirthday();
        return !(pl.trim().isEmpty()
                || pl.length() > 12
                || player.getTitle().length() > 30
                || plExp < 0
                || plExp > 10_000_000
                || birth.before(new GregorianCalendar(2000, Calendar.JANUARY, 1).getTime())
                || birth.after(new GregorianCalendar(3000, Calendar.DECEMBER, 31).getTime()));
    }

    @GetMapping("/players/count")
    public long getCount(@RequestParam Map<String, String> allParams){
        return playerService.getCount(allParams);
    }
}
