package com.example.inventory_app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.inventory_app.entity.AppUser;
import com.example.inventory_app.entity.Store;
import com.example.inventory_app.repository.AppUserRepository;
import com.example.inventory_app.repository.StoreRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final StoreRepository storeRepository;

    public DataInitializer(AppUserRepository appUserRepository,
                           StoreRepository storeRepository) {
        this.appUserRepository = appUserRepository;
        this.storeRepository = storeRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // 1) Ensure at least one user exists
        AppUser defaultUser = appUserRepository
                .findByEmail("owner@example.com")
                .orElseGet(() -> {
                    AppUser user = new AppUser(
                            "owner@example.com",
                            "TEMP_HASH_ONLY_FOR_DEV"  // real password/auth later
                    );
                    return appUserRepository.save(user);
                });

        // 2) Attach this user as owner to any store that doesn't have one yet
        for (Store store : storeRepository.findAll()) {
            if (store.getOwner() == null) {
                store.setOwner(defaultUser);
                storeRepository.save(store);
            }
        }
    }
}
