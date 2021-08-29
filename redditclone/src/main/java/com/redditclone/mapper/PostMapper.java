package com.redditclone.mapper;


import com.redditclone.dto.PostRequest;
import com.redditclone.dto.PostResponse;
import com.redditclone.model.Post;
import com.redditclone.model.Subreddit;
import com.redditclone.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {


    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source = "postRequest.description")
    Post map(PostRequest postRequest, Subreddit subreddit, User user);

    @Mapping(target = "id", source = "postId")
    @Mapping(target = "subredditName", source = "subreddit.name")
    @Mapping(target = "userName", source = "user.username")
        PostResponse mapToDto(Post post);

}
