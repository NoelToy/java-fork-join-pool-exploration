package com.noel.forkjoinpoolinterrupt.controllers;

import com.noel.forkjoinpoolinterrupt.services.MasterService;
import com.noel.forkjoinpoolinterrupt.services.NormalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(path = "/endApp")
public class EndAppController {

    @Autowired
    private MasterService masterService;

    @RequestMapping(path = "/taskInvoker",method = RequestMethod.GET)
    public ResponseEntity<Map<Integer,Integer>> invokeForkJoin(){
        try {
            masterService.executeTask();
            return ResponseEntity.ok(masterService.executeTask());
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
    @RequestMapping(path = "/taskInvokerNormal",method = RequestMethod.GET)
    public ResponseEntity<Map<Integer,Integer>> invokeForkJoinNormal(){
        try {
            NormalService normalService = new NormalService();
            //normalService.executeTask("0.1");
            return ResponseEntity.ok(normalService.executeTask("0.2"));
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
    @RequestMapping(path = "/taskInvokerFuture",method = RequestMethod.GET)
    public ResponseEntity<String> invokeForkJoinFuture(){
        try {
            NormalService normalService = new NormalService();
            CompletableFuture<Map<Integer, Integer>> future = normalService.executeTaskFuture("0.1");
            future.thenAccept(result->{
                System.out.println("Task Completed Successfully!");
                System.out.println(result);
            });
            future.exceptionally(throwable -> {
                System.out.println("Exception occurred during task execution: " + throwable.getMessage());
                return null;
            });
            return ResponseEntity.ok("Task Submitted!!");
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @RequestMapping(path = "/taskInvokerFutureReturn",method = RequestMethod.GET)
    public ResponseEntity<Map<Integer,Integer>> invokeForkJoinFutureReturn(){
        try {
            NormalService normalService = new NormalService();
            CompletableFuture<Map<Integer, Integer>> future = normalService.executeTaskFuture("0.1");
            CompletableFuture<Map<Integer, Integer>> future_2 = normalService.executeTaskFuture("0.2");
            future.thenAccept(result->{
                System.out.println("Task 1 Completed Successfully!");
                //System.out.println(result);
            });

            future_2.thenAccept(result->{
                System.out.println("Task 2 Completed Successfully!");
            });

            future_2.exceptionally(throwable -> {
                System.out.println("Exception occurred during task execution: " + throwable.getMessage());
                return null;
            });
            return future.thenApply(ResponseEntity::ok).get();
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
}
