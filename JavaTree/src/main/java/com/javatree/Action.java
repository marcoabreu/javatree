package com.javatree;

import java.util.function.Supplier;

/**
 * Basic composite to actually *do* something
 * Created by AbreuM on 30.06.2016.
 */
public class Action extends Composite {
    private final Supplier<Boolean> runnable;

    /**
     * Instantiate an Action with the passed runnable
     * @param runnable Method which will be executed. Return true if the action succeeded, false otherwise
     */
    public Action(Supplier<Boolean> runnable) {
        this.runnable = runnable;
    }

    @Override
    protected boolean start() {
        if(this.runnable == null) {
            throw new IllegalArgumentException("No runnable defined");
        }

        return true;
    }

    @Override
    protected boolean run() {
        return runnable.get();
    }

    @Override
    protected void stop() {

    }
}
