package com.redditclone.service;


import com.redditclone.dto.SubredditDto;
import com.redditclone.exceptions.SpringRedditException;
import com.redditclone.mapper.SubredditMapper;
import com.redditclone.model.Subreddit;
import com.redditclone.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {

    private final SubredditRepository subredditRepository;
    private final SubredditMapper subredditMapper;

    @Transactional
    public SubredditDto save(SubredditDto subredditDto) {
        Subreddit save = subredditRepository.save(subredditMapper.maptDtotoSubreddit(subredditDto));
        subredditDto.setId(save.getId());
        return subredditDto;
    }



    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {
        return subredditRepository.findAll()
                .stream()
                .map(subredditMapper::mapSubredditToDto)
                .collect(toList());
    }

    public SubredditDto getSubreddit(Long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SpringRedditException("No subreddit found with id " + id));
        return  subredditMapper.mapSubredditToDto(subreddit);
    }
}