package com.javatree;

import java.util.concurrent.Callable;

/**
 * Created by AbreuM on 30.06.2016.
 */
public abstract class Composite implements Callable<RunStatus> {
    protected String name;
    protected String description;
    /**
     * Prepare composite to run
     * @return True if start successful, false if failed and composite should not be run
     */
    protected abstract boolean start() ;

    /**
     * Execute composite until it has reached a final state
     * @return True if run successful, false if failed
     */
    protected abstract boolean run() throws Exception;

    /**
     * Clean up after an execution if composite if composite started successfully
     */
    protected abstract void stop();

    /**
     * Blocking-Method to execute whole lifecycle of this composite, do not call manually!
     * @return Status of the execution
     * @throws Exception
     */
    @Override
    public RunStatus call() throws Exception {
        if(!start()) {
            return RunStatus.FAILURE;
        }

        boolean runSuccessful = run();

        stop();

        return runSuccessful ? RunStatus.SUCCESS : RunStatus.FAILURE;
    }
}
