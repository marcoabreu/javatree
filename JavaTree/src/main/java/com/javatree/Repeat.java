package com.javatree;

import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * GroupComposite which  executes children repetitively
 * Created by AbreuM on 01.07.2016.
 */
public class Repeat extends Sequence {
    public static final int REPEAT_INDICATOR = -1;
    private final int nbRepeats;
    private final BooleanSupplier successCondition;
    private boolean requireSucceededCondition;

    private int currentRun = 0;

    /**
     * Repeat children infinitely or until any fails
     * @param children Children to execute
     */
    public Repeat(Composite... children) {
        this(REPEAT_INDICATOR, null, false, children);
    }

    /**
     * Repeat children infinitely or until any fails
     * @param children Children to execute
     */
    public Repeat(List<Composite> children) {
        this(REPEAT_INDICATOR, null, false, children);
    }

    /**
     * Repeat children until number of repeats is reached. Fails if a child fails.
     * @param nbRepeats Times to repeat
     * @param children Children to execute
     */
    public Repeat(int nbRepeats, Composite... children) {
        this(nbRepeats, null, false, children);
    }

    /**
     * Repeat children until number of repeats is reached. Fails if a child fails.
     * @param nbRepeats Times to repeat
     * @param children Children to execute
     */
    public Repeat(int nbRepeats, List<Composite> children) {
        this(nbRepeats, null, false, children);
    }

    /**
     * Repeat children until condition is met. Fails if a child fails.
     * @param successCondition Condition to interrupt loop earlier
     * @param children Children to execute
     */
    public Repeat(BooleanSupplier successCondition, Composite... children) {
        this(REPEAT_INDICATOR, successCondition, true, children);
    }

    /**
     * Repeat children until condition is met. Fails if a child fails.
     * @param successCondition Condition to interrupt loop earlier
     * @param children Children to execute
     */
    public Repeat(BooleanSupplier successCondition, List<Composite> children) {
        this(REPEAT_INDICATOR, successCondition, true, children);
    }

    /**
     * Repeat children until number of repeats is reached OR condition is met. Both cases will be a success! Fails if a child fails.
     * @param nbRepeats Times to repeat
     * @param successCondition Condition to interrupt loop earlier
     * @param requireSucceededCondition May a run only be successful if the condition was met
     * @param children Children to execute
     */
    public Repeat(int nbRepeats, BooleanSupplier successCondition, boolean requireSucceededCondition, Composite... children) {
        super(children);

        if(nbRepeats <= 0 && nbRepeats != REPEAT_INDICATOR) {
            throw new IllegalArgumentException("nbRepeats may only be positive. Use REPEAT_INDICATOR if you need an infinite loop");
        }

        if(requireSucceededCondition && successCondition == null) {
            throw new IllegalArgumentException("successCondition required");
        }

        this.nbRepeats = nbRepeats;
        this.successCondition = successCondition;
        this.requireSucceededCondition = requireSucceededCondition;


    }

    /**
     * Repeat children until number of repeats is reached OR condition is met. Both cases will be a success! Fails if a child fails.
     * @param nbRepeats Times to repeat
     * @param successCondition Condition to interrupt loop earlier
     * @param requireSucceededCondition May a run only be successful if the condition was met
     * @param children Children to execute
     */
    public Repeat(int nbRepeats, BooleanSupplier successCondition, boolean requireSucceededCondition, List<Composite> children) {
        super(children);

        if(nbRepeats <= 0 && nbRepeats != REPEAT_INDICATOR) {
            throw new IllegalArgumentException("nbRepeats may only be positive. Use REPEAT_INDICATOR if you need an infinite loop");
        }

        if(requireSucceededCondition && successCondition == null) {
            throw new IllegalArgumentException("successCondition required");
        }

        this.nbRepeats = nbRepeats;
        this.successCondition = successCondition;
        this.requireSucceededCondition = requireSucceededCondition;
    }

    @Override
    public RunStatus call() throws Exception {
        for(currentRun = 0; nbRepeats == REPEAT_INDICATOR || currentRun < nbRepeats; currentRun++) {
            //Condition has been met, no need to repeat any further
            if(successCondition != null && successCondition.getAsBoolean()) {
                break;
            }

            //Child failed
            if(super.call() == RunStatus.FAILURE) {
                return RunStatus.FAILURE;
            }
        }

        if(requireSucceededCondition && successCondition != null) {
            return successCondition.getAsBoolean() ? RunStatus.SUCCESS : RunStatus.FAILURE;
        } else {
            //All repeats executed or successCondition was met, this was a successful run
            return RunStatus.SUCCESS;
        }


    }
}
