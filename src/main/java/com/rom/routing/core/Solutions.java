package com.rom.routing.core;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a collection of {@link Solution}, where each item corresponds to a query.
 * Keeps them in the insertion order of ({@link #addSolution(Solution)}). The only way to iterate over the solutions
 * in that order is to pass {@link SolutionVisitor} to {@link #runSolutionVisitor(SolutionVisitor)}.
 *
 * @author Roman Katerinenko
 */
public class Solutions {
    private final Collection<Solution> solutions = new ArrayList<>();

    public void addSolution(Solution solution) {
        solutions.add(solution);
    }

    public void runSolutionVisitor(SolutionVisitor solutionVisitor) {
        solutions.forEach(s -> s.accept(solutionVisitor));
    }

    public boolean isEmpty() {
        return solutions.isEmpty();
    }

    public interface SolutionVisitor {
        void visit(PathSolution pathSolution);

        void visit(EmptyRouteSolution emptyRouteSolution);

        void visit(NearbySolution nearbySolution);

        void visit(EmptyNearbySolution emptyNearbySolution);
    }
}