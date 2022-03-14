package edu.byu.cs.tweeter.client.model.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaseService {

    protected void executeTask(Runnable task) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(task);
    }
}
