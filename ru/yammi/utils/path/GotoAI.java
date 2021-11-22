package ru.yammi.utils.path;

import net.minecraft.util.BlockPos;

import java.text.DecimalFormat;

import net.minecraft.entity.EntityLivingBase;

public class GotoAI
{
    private APathFinder pathFinder;
    private APathProcessor processor;
    private boolean done;
    private boolean failed;
    private EntityLivingBase target;

    public static int removeDecimals(final double number) {
        final DecimalFormat decimalFormat = new DecimalFormat("####################################");
        return Integer.parseInt(decimalFormat.format(number));
    }

    public GotoAI(final BlockPos goal) {
        this.pathFinder = new APathFinder(goal);
    }

    public GotoAI(final EntityLivingBase entity) {
        this.pathFinder = new APathFinder(new BlockPos((int)entity.posX, removeDecimals(entity.posY), (int)entity.posZ));
        this.target = entity;
    }

    public void update() {
        if (!this.pathFinder.isDone()) {
            if (this.processor != null) {
                this.processor.lockControls();
            }
            this.pathFinder.think();
            if (!this.pathFinder.isDone()) {
                if (this.pathFinder.isFailed()) {
                    this.failed = true;
                }
                return;
            }
            this.pathFinder.formatPath();
            this.processor = this.pathFinder.getProcessor();
        }
        if (this.processor != null && !this.pathFinder.isPathStillValid(this.processor.getIndex())) {
            this.pathFinder = new APathFinder(this.pathFinder.getGoal());
            return;
        }
        this.processor.process();
        if (this.processor.isFailed()) {
            this.failed = true;
        }
        if (this.processor.isDone()) {
            this.done = true;
        }
    }

    public void update(final String processor) {
        if (!this.pathFinder.isDone()) {
            if (this.processor != null) {
                this.processor.lockControls();
            }
            this.pathFinder.think();
            if (!this.pathFinder.isDone()) {
                if (this.pathFinder.isFailed()) {
                    this.failed = true;
                }
                return;
            }
            this.pathFinder.formatPath();
            if (processor.equalsIgnoreCase("infiniteaura")) {
                this.processor = this.pathFinder.getInfiniteAuraProcessor(this.target);
            }
            else {
                this.processor = this.pathFinder.getProcessor(processor);
            }
        }
        if (this.processor != null && !this.pathFinder.isPathStillValid(this.processor.getIndex())) {
            this.pathFinder = new APathFinder(this.pathFinder.getGoal());
            return;
        }
        this.processor.process();
        if (this.processor.isFailed()) {
            this.failed = true;
        }
        if (this.processor.isDone()) {
            this.done = true;
        }
    }

    public void stop() {
        if (this.processor != null) {
            this.processor.stop();
        }
    }

    public final boolean isDone() {
        return this.done;
    }

    public final boolean isFailed() {
        return this.failed;
    }
}
