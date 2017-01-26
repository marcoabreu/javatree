package com.javatree;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Executor to run Composites, best use via try-with-resources
 * Created by AbreuM on 30.06.2016.
 */
public class Executor implements AutoCloseable {
    private static ExecutorService threadPool = Executors.newCachedThreadPool();
    private final Composite composite;
    private Future<RunStatus> executingFuture;
    private ExecutionException lastExecutionException;

    public Executor(Composite compositeToExecute) {
        if(compositeToExecute == null) {
            throw new IllegalArgumentException("No composite passed");
        }

        this.composite = compositeToExecute;
    }

    /**
     * Starts the executor and aborts previous tasks
     */
    public void start() {
        //Clean up
        if(executingFuture != null) {
            abort();
        }

        executingFuture = threadPool.submit(composite);
    }

    /**
     * Method to get the status of the underlying composite
     * @param msToWait maximum time to wait for the result
     * @return Current execution status
     */
    public RunStatus execute(long msToWait) {
        try {
            return execute(msToWait, false);
        } catch (ExecutionException e) {
            throw new RuntimeException("Can not happen");
        }
    }

    /**
     * Method to get the status of the underlying composite
     * @param msToWait maximum time to wait for the result
     * @param chainExceptions True to directly thrown execution exceptions, false to just return failure and access exception later via getLastExecutionException()
     * @return Current execution status
     */
    public RunStatus execute(long msToWait, boolean chainExceptions) throws ExecutionException {
        if(executingFuture == null) {
            throw new IllegalStateException("Start the executor first");
        }

        if(executingFuture.isCancelled()) {
            return RunStatus.FAILURE;
        }

        try {
            return executingFuture.get(msToWait, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return RunStatus.FAILURE;
        } catch (TimeoutException e) {
            return RunStatus.RUNNING;
        } catch(ExecutionException e) {
            //Abort execution because we ran into an error
            this.lastExecutionException = e;
            abort();

            if(chainExceptions) {
                throw e;
            } else {
                return RunStatus.FAILURE;
            }

        }
    }

    /**
     * Abort currently executed composite
     */
    public void abort() {
        if(executingFuture == null) {
            throw new IllegalStateException("Start the executor first");
        }

        if(executingFuture.cancel(true)) {
            this.composite.stop();
        };
    }

    /**
     * Cleans up the state of this executor
     */
    public void cleanUp() {
        if(executingFuture != null) {
            abort();
        }

        executingFuture = null;
    }

    @Override
    public void close() throws Exception {
        cleanUp();
    }

    public ExecutionException getLastExecutionException() {
        try {
            execute(0);
        } catch(Exception e) {
        }

        return lastExecutionException;
    }

    /**
     * Abort all tasks running in the thread pool
     */
    public static void abortThreadPool() {
        threadPool.shutdownNow();
    }

    /**
     * Start a new thread pool
     */
    public static void startThreadPool() {
        threadPool = Executors.newCachedThreadPool();
    }
}
