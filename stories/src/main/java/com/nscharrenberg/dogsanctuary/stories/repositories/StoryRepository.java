package com.nscharrenberg.dogsanctuary.stories.repositories;

import com.nscharrenberg.dogsanctuary.stories.models.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findAllByDogsInOrderByHappenedAtDescCreatedAtDesc(String name);
}
