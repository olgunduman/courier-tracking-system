package com.courier.tracking.service;


import com.courier.tracking.model.Store;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
public class StoreServiceTest {
    private List<Store> stores;

    @PostConstruct
    public void loadStores() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream("stores.json");
        stores = Arrays.asList(mapper.readValue(is, Store[].class));
    }

    public List<Store> getStores() {
        return stores;
    }
}