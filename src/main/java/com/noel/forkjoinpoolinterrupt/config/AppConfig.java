package com.noel.forkjoinpoolinterrupt.config;

import com.noel.forkjoinpoolinterrupt.services.AppShutdownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class AppConfig {
    @Autowired
    private AppShutdownService appShutdownService;
    @PreDestroy
    public void onShutdown(){
        appShutdownService.onAppShutdown();
    }
}
