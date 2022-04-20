package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class PlayerSpecification {
    public Specification<Player> getPlayers(Map<String, String> allParams){
        String name = null;
        String title = null;
        Race race = null;
        Profession profession = null;
        Long after = null;
        Long before = null;
        Boolean banned = null;
        Integer minExperience = null;
        Integer maxExperience = null;
        Integer minLevel = null;
        Integer maxLevel = null;
        PlayerOrder order = null;

        try {
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                switch (entry.getKey()){
                    case "name":
                        name = entry.getValue(); break;
                    case "title":
                        title = entry.getValue(); break;
                    case "race":
                        race = Race.valueOf(entry.getValue()); break;
                    case "profession":
                        profession = Profession.valueOf(entry.getValue()); break;
                    case "after":
                        after = Long.parseLong(entry.getValue()); break;
                    case "before":
                        before = Long.parseLong(entry.getValue()); break;
                    case "banned":
                        banned = Boolean.valueOf(entry.getValue()); break;
                    case "minExperience":
                        minExperience = Integer.parseInt(entry.getValue()); break;
                    case "maxExperience":
                        maxExperience = Integer.parseInt(entry.getValue()); break;
                    case "minLevel":
                        minLevel = Integer.parseInt(entry.getValue()); break;
                    case "maxLevel":
                        maxLevel = Integer.parseInt(entry.getValue()); break;
                    case "order":
                        order = PlayerOrder.valueOf(entry.getValue()); break;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        String finalName = name;
        PlayerOrder finalOrder = order;
        String finalTitle = title;
        Race finalRace = race;
        Profession finalProfession = profession;
        Long finalAfter = after;
        Long finalBefore = before;
        Boolean finalBanned = banned;
        Integer finalMinExperience = minExperience;
        Integer finalMaxExperience = maxExperience;
        Integer finalMinLevel = minLevel;
        Integer finalMaxLevel = maxLevel;

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (finalName != null) predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + finalName.toLowerCase() + "%"));
            if (finalTitle != null) predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + finalTitle.toLowerCase() + "%"));
            if (finalRace != null) predicates.add(criteriaBuilder.equal(root.get("race"), finalRace));
            if (finalProfession != null) predicates.add(criteriaBuilder.equal(root.get("profession"), finalProfession));
            if (finalAfter != null) predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), new Date(finalAfter)));
            if (finalBefore != null) predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), new Date(finalBefore)));
            if (finalBanned != null) predicates.add(criteriaBuilder.equal(root.get("banned"), finalBanned));
            if (finalMinExperience != null) predicates.add(criteriaBuilder.ge(root.get("experience"), finalMinExperience));
            if (finalMaxExperience != null) predicates.add(criteriaBuilder.le(root.get("experience"), finalMaxExperience));
            if (finalMinLevel != null) predicates.add(criteriaBuilder.ge(root.get("level"), finalMinLevel));
            if (finalMaxLevel != null) predicates.add(criteriaBuilder.le(root.get("level"), finalMaxLevel));

            String field = "id";
            if (finalOrder != null && finalOrder != PlayerOrder.ID) {
                switch (finalOrder){
                    case NAME:
                        field = "name"; break;
                    case LEVEL:
                        field = "level"; break;
                    case BIRTHDAY:
                        field = "birthday"; break;
                    case EXPERIENCE:
                        field = "experience"; break;
                }
            }
            query.orderBy(criteriaBuilder.asc(root.get(field)));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
