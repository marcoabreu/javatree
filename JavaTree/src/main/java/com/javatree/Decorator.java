package com.javatree;

import java.util.function.Supplier;

/**
 * Created by AbreuM on 30.06.2016.
 */
public class Decorator extends Composite {
    private Supplier<Boolean> condition;
    private Composite successComposite; //Composite to execute if condition succeeds
    private Composite failComposite; //Composite to execute if condition fails

    private Composite childToExecute; //Current executed child

    public Decorator(Supplier<Boolean> condition, Composite successComposite) {
        this(condition, successComposite, null);
    }

    public Decorator(Supplier<Boolean> condition, Composite successComposite, Composite failComposite) {
        this.condition = condition;
        this.successComposite = successComposite;
        this.failComposite = failComposite;
    }

    @Override
    public boolean start() {
        if(condition == null) {
            throw new IllegalArgumentException("No condition defined");
        }

        if(successComposite == null) {
            throw new IllegalArgumentException("No success composite defined");
        }

        if(condition.get()) {
            childToExecute = successComposite;
        } else {
            childToExecute = failComposite;
        }

        return true; //An if-statement does not fail if the condition is not met, rather does it jump over
        //return childToExecute != null; //Fail if there is no child to execute
    }

    @Override
    public boolean run() throws Exception {
        //Nothing to do
        if(childToExecute == null) {
            return true;
        }

        try (Executor executor = new Executor(childToExecute)) {
            executor.start();
            while (executor.execute(100, true) == RunStatus.RUNNING) {
            }
            return executor.execute(0, true) == RunStatus.SUCCESS;
        }
    }

    @Override
    public void stop() {
        childToExecute = null;
    }

    public Composite getSuccessComposite() {
        return successComposite;
    }

    public void setSuccessComposite(Composite successComposite) {
        this.successComposite = successComposite;
    }

    public Composite getFailComposite() {
        return failComposite;
    }

    public void setFailComposite(Composite failComposite) {
        this.failComposite = failComposite;
    }
}
