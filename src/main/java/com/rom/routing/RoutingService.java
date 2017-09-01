package com.rom.routing;

import com.rom.routing.core.PathSolution;
import com.rom.routing.core.PathTask;
import com.rom.routing.core.Solutions;
import com.rom.routing.io.SolutionsWriter;
import com.rom.routing.io.TaskParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Stateless class representing entry point to the service ({@link #findPath(InputStream, OutputStream)}).
 * writing formatted solution ({@link PathSolution}) to output.
 * <p>
 * It doesn't distinguish types of error, just outputs {@link #WRONG_INPUT_ERROR_MESSAGE}.
 *
 * @author Roman Katerinenko
 * @see PathSolution
 * @see PathTask
 */
public final class RoutingService {
    private static final Logger logger = LoggerFactory.getLogger(RoutingService.class);

    public static final String CHARSET = "UTF-8";

    private static final String WRONG_INPUT_ERROR_MESSAGE = "Error: wrong input";
    private static final String NO_SOLUTIONS_MESSAGE = WRONG_INPUT_ERROR_MESSAGE;

    private RoutingService() {
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        RoutingService.findPath(System.in, System.out);
    }

    public static void findPath(InputStream taskDescription, OutputStream outputStream) {
        PathTask pathTask = TaskParser.parse(taskDescription);
        String resultString;
        if (pathTask != null) {
            Solutions solutions = solve(pathTask);
            resultString = describe(solutions);
        } else {
            resultString = WRONG_INPUT_ERROR_MESSAGE;
        }
        try {
            outputStream.write(resultString.getBytes(CHARSET));
        } catch (IOException e) {
            logger.error("Unable to write result", e);
        }
    }

    private static Solutions solve(PathTask pathTask) {
        DijkstraQuerySolver querySolver = new DijkstraQuerySolver(createGraphFor(pathTask));
        pathTask.runQueryVisitor(querySolver);
        return querySolver.getSolutions();
    }

    private static Graph createGraphFor(PathTask pathTask) {
        return new GraphBuilder(pathTask).build();
    }

    private static String describe(Solutions solutions) {
        if (solutions != null && !solutions.isEmpty()) {
            SolutionsWriter visitor = new SolutionsWriter();
            solutions.runSolutionVisitor(visitor);
            return visitor.getString();
        } else {
            return NO_SOLUTIONS_MESSAGE;
        }
    }
}