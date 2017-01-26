package com.javatree;

import java.util.*;

/**
 * Composite which executes other composites in sequence
 * Created by AbreuM on 30.06.2016.
 */
public class Sequence extends Composite {
    private List<Composite> children;

    private Deque<Composite> currentChildren;
    private Composite runningChild;

    public Sequence(Composite... children) {
        this.children = Arrays.asList(children);
    }

    public Sequence(List<Composite> children) {
        this.children = children;
    }

    @Override
    public boolean start() {
        currentChildren = new ArrayDeque<>(children);

        return true;
    }

    @Override
    public boolean run() throws Exception {
        while((runningChild = currentChildren.poll()) != null) {
            try (Executor currentExecutor = new Executor(runningChild)) {
                currentExecutor.start();

                while (currentExecutor.execute(100, true) == RunStatus.RUNNING) {
                    onTickChild(runningChild);
                }

                //Child failed, this Sequence failed
                if(currentExecutor.execute(0, true) == RunStatus.FAILURE) {
                    return false;
                }
            } catch (InterruptedException e) {
                return false; //We couldn't finish our work and got interrupted
            }

            //All children have to succeed in order to let this run be successful

        }

        return true; //All children executed, everything went well
    }

    /**
     * Called every time a child is ticked
     * @param childComposite
     */
    protected void onTickChild(Composite childComposite) {
    }

    @Override
    public void stop() {
        currentChildren.clear();
        runningChild = null;
    }

    /**
     * Replaces a child with the passed composite while keeping the sequence in order
     * @param childToReplace Child to be replaced
     * @param newChild New child
     * @throws IllegalStateException Thrown when called while composite is running
     * @throws NoSuchElementException Thrown when composite is not a child of this sequence
     */
    public void replaceChild(Composite childToReplace, Composite newChild) {
        if(currentChildren != null && !currentChildren.isEmpty()) {
            throw new IllegalStateException("You may not replace a child of a running composite");
        }

        int indexToReplace = children.indexOf(childToReplace);

        if(indexToReplace == -1) {
            throw new NoSuchElementException("Composite not found");
        }

        children.set(indexToReplace, newChild);
    }

    public List<Composite> getChildren() {
        return children;
    }

    public void setChildren(List<Composite> children) {
        this.children = children;
    }
}
