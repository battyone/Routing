package com.rom.routing.core;

/**
 * @author Roman Katerinenko
 */
public interface Solution {
    void accept(Solutions.SolutionVisitor visitor);
}