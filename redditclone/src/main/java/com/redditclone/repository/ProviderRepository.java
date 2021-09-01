package com.redditclone.repository;

import com.redditclone.model.Provider;
import com.redditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
    Optional<Provider> findByProviderName(String providerName);
}
