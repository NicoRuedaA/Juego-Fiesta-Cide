package com.TETOSOFT.graphics;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

/**
 * A time-based sprite animation made up of a sequence of frames.
 */
public class Animation {

    private final List<AnimFrame> frames;
    private int  currFrameIndex;
    private long animTime;
    private long totalDuration;

    public Animation() {
        frames = new ArrayList<>();
    }

    /** Private constructor used by {@link #clone()}. */
    private Animation(List<AnimFrame> frames, long totalDuration) {
        this.frames        = new ArrayList<>(frames);
        this.totalDuration = totalDuration;
        start();
    }

    public Object clone() {
        return new Animation(frames, totalDuration);
    }

    public synchronized void addFrame(Image image, long duration) {
        totalDuration += duration;
        frames.add(new AnimFrame(image, totalDuration));
    }

    public synchronized void start() {
        animTime       = 0;
        currFrameIndex = 0;
    }

    public synchronized void update(long elapsedTime) {
        if (frames.size() <= 1) return;

        animTime += elapsedTime;

        if (animTime >= totalDuration) {
            animTime = animTime % totalDuration;
            currFrameIndex = 0;
        }

        while (animTime > getFrame(currFrameIndex).endTime) {
            currFrameIndex++;
        }
    }

    /**
     * Advances the walk cycle using only the first {@code walkFrameCount} frames,
     * ignoring any trailing idle/jump frames that should never auto-advance.
     *
     * @param walkFrameCount number of frames that belong to the walk cycle (e.g. 3)
     */
    /**
     * Avanza el ciclo de animación usando solo los primeros {@code walkFrameCount} frames.
     * Pasar {@code frames.size()} para usar todos los frames disponibles.
     */
    public synchronized void updateWalkCycle(long elapsedTime, int walkFrameCount) {
        if (frames.isEmpty()) return;

        walkFrameCount = Math.min(walkFrameCount, frames.size());

        if (currFrameIndex >= walkFrameCount) {
            currFrameIndex = 0;
            animTime = 0;
        }

        animTime += elapsedTime;

        long walkDuration = getFrame(walkFrameCount - 1).endTime;
        if (animTime >= walkDuration) {
            animTime = animTime % walkDuration;
            currFrameIndex = 0;
        }

        while (currFrameIndex < walkFrameCount - 1
                && animTime > getFrame(currFrameIndex).endTime) {
            currFrameIndex++;
        }
    }

    /** Sobrecarga que usa todos los frames — para enemigos simples. */
    public synchronized void updateWalkCycle(long elapsedTime) {
        updateWalkCycle(elapsedTime, frames.size());
    }

    public synchronized int getCurrFrameIndex() { return currFrameIndex; }

    public synchronized Image getImage() {
        if (frames.isEmpty()) return null;
        return getFrame(currFrameIndex).image;
    }

    public synchronized void setCurrFrame(int index) {
        if (index >= 0 && index < frames.size()) {
            currFrameIndex = index;
            animTime = (index == 0) ? 0 : getFrame(index - 1).endTime;
        }
    }

    private AnimFrame getFrame(int i) {
        return frames.get(i);
    }

    // -------------------------------------------------------------------------

    private static class AnimFrame {
        final Image image;
        final long  endTime;

        AnimFrame(Image image, long endTime) {
            this.image   = image;
            this.endTime = endTime;
        }
    }
}
