package com.noel.forkjoinpoolinterrupt.services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class MasterService {
    public Map<Integer,Integer> executeTask(){
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        List<Integer> numberList = getNumbersUsingIntStreamRange(0,10000);
        System.out.println("List Size: "+numberList.size());
        Map<Integer,Integer> result = new ConcurrentHashMap<>();
        final boolean[] setSleep = {false};
        try {
            ForkJoinTask<?> joinTask = forkJoinPool.submit(() -> {
                try {
                    numberList.stream().parallel().forEach(val->{
                        System.out.println(Thread.currentThread().getName()+": "+val);
                                result.put(val,val*2);
                                if(Thread.currentThread().getName().contains("worker-0") && !setSleep[0]){
                                    setSleep[0] = true;
                                    try {
                                        Thread.sleep(5000);
                                    } catch (InterruptedException e) {
                                        System.out.println("Got Interrupted!!");
                                        e.printStackTrace();
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
            joinTask.get();
            forkJoinPool.shutdown();
            return result;
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            return result;
        }
    }
    public List<Integer> getNumbersUsingIntStreamRange(int start, int end) {
        return IntStream.range(start, end)
                .boxed()
                .collect(Collectors.toList());
    }
}
