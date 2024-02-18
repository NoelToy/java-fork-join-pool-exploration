package com.noel.forkjoinpoolinterrupt.services;

import org.springframework.stereotype.Service;

@Service
public class AppShutdownService {
    public void onAppShutdown(){
        System.out.println("Shutdown Service Called");
    }
}
