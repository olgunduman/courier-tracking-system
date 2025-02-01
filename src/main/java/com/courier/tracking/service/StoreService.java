package com.courier.tracking.service;


import com.courier.tracking.dto.Store;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Getter
@Service
@Slf4j
@RequiredArgsConstructor
public class StoreService {
    private List<Store> stores;

    @PostConstruct
    public void loadStores() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("stores.json")) {
            ObjectMapper mapper = new ObjectMapper();
            stores = Arrays.asList(mapper.readValue(is, Store[].class));
        } catch (Exception e) {
            log.error("Error loading stores", e);
            throw e;
        }
    }

}