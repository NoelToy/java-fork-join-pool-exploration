package com.noel.forkjoinpoolinterrupt.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NormalService {
    List<ForkJoinTask<?>> forkJoinTasks = new ArrayList<>();
    public Map<Integer,Integer> executeTask(String tag){
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        List<Integer> numberList = getNumbersUsingIntStreamRange(0,10000);
        Map<Integer,Integer> result = new ConcurrentHashMap<>();
        final boolean[] setSleep = {false};
        try {
            //System.out.println("Object Tag: "+tag);
            ForkJoinTask<?> joinTask = forkJoinPool.submit(() -> {
                try {
                    numberList.stream().parallel().forEach(val->{
                        if (!Thread.currentThread().isInterrupted()) {
                            System.out.println(tag+"---->"+Thread.currentThread().getName()+": "+val);
                            result.put(val,val*2);
                            if(Thread.currentThread().getName().contains("worker") && !setSleep[0]){
                                setSleep[0] = true;
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt(); // Preserve interruption status
                                    System.out.println("Got Interrupted!!");
                                    e.printStackTrace();
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
            forkJoinTasks.add(joinTask);
            //joinTask.get();
            //joinTask.join();
            //joinTask.cancel(true);
            //Thread.sleep(8000);
            forkJoinPool.shutdown();
            if (joinTask.isCancelled()) {
                System.out.println("Task canceled successfully.");
            }
            else {
                System.out.println("Task could not be canceled.");
            }
            return result;
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            return result;
        }
    }
    public CompletableFuture<Map<Integer, Integer>> executeTaskFuture(String tag){
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        List<Integer> numberList = getNumbersUsingIntStreamRange(0,10000);
        Map<Integer,Integer> result = new ConcurrentHashMap<>();
        final boolean[] setSleep = {false};

        CompletableFuture<Map<Integer, Integer>> future = new CompletableFuture<>();

        try {
            ForkJoinTask<?> joinTask = forkJoinPool.submit(() -> {
                try {
                    numberList.stream().parallel().forEach(val->{
                        System.out.println(tag+"---->"+Thread.currentThread().getName()+": "+val);
                        result.put(val,val*2);
                        if(Thread.currentThread().getName().contains("worker") && !setSleep[0]){
                            setSleep[0] = true;
                            try {
                                Thread.sleep(5000);
                                //throw new RuntimeException("Manual Exception");
                            } catch (InterruptedException e) {
                                System.out.println("Got Interrupted!!");
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        }
                    });
                    future.complete(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    future.completeExceptionally(e);
                }
            });
            //joinTask.cancel(true);
            forkJoinPool.shutdown();
            return future;
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            future.completeExceptionally(e);
            return future;
        }

    }
    public List<Integer> getNumbersUsingIntStreamRange(int start, int end) {
        return IntStream.range(start, end)
                .boxed()
                .collect(Collectors.toList());
    }
}
