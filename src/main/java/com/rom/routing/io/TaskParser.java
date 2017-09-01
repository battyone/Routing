package com.rom.routing.io;

import com.rom.routing.core.NearbyQuery;
import com.rom.routing.core.Path;
import com.rom.routing.core.PathQuery;
import com.rom.routing.core.PathTask;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;

import static com.rom.routing.RoutingService.CHARSET;
import static java.util.regex.Pattern.UNICODE_CASE;
import static java.util.regex.Pattern.UNICODE_CHARACTER_CLASS;
import static java.util.regex.Pattern.compile;

/**
 * @author Roman Katerinenko
 */
public class TaskParser {
    private static final Pattern STATION_NAME_PATTERN = compile("[\\w[0-9]]+", UNICODE_CHARACTER_CLASS | UNICODE_CASE);
    private static final Pattern STATION_NAME_WITH_COMMA_PATTERN = compile("\\w+,", UNICODE_CHARACTER_CLASS | UNICODE_CASE);
    private static final Pattern STATION_NAME_WITH_COLON_PATTERN = compile("\\w+:", UNICODE_CHARACTER_CLASS | UNICODE_CASE);
    private static final Pattern EDGE_SIGN_PATTERN = compile("->");
    private static final String ROUTE_QUERY_KEYWORD = "path";
    private static final String NEARBY_QUERY_KEYWORD = "near";

    private TaskParser() {
        throw new IllegalStateException("Don't call this constructor");
    }

    /**
     * @param inputStream input stream
     * @return null in case of any error during parsing, otherwise not null.
     */
    public static PathTask parse(InputStream inputStream) {
        PathTask pathTask = new PathTask();
        try (Scanner scanner = new Scanner(inputStream, CHARSET)) {
            int edgesAmount = scanner.nextInt();
            for (int i = 0; i < edgesAmount; i++) {
                String fromStation = scanner.next(STATION_NAME_PATTERN);
                scanner.next(EDGE_SIGN_PATTERN);
                String toStation = scanner.next(STATION_NAME_WITH_COLON_PATTERN);
                toStation = toStation.substring(0, toStation.length() - 1);
                int travelTime = scanner.nextInt();
                pathTask.addRoute(new Path(fromStation, toStation, travelTime));
            }
            while (scanner.hasNext()) {
                String queryType = scanner.next();
                switch (queryType) {
                    case ROUTE_QUERY_KEYWORD:
                        String fromStation = scanner.next(STATION_NAME_PATTERN);
                        scanner.next(EDGE_SIGN_PATTERN);
                        String toStation = scanner.next(STATION_NAME_PATTERN);
                        pathTask.addQuery(new PathQuery(fromStation, toStation));
                        break;
                    case NEARBY_QUERY_KEYWORD:
                        fromStation = scanner.next(STATION_NAME_WITH_COMMA_PATTERN);
                        fromStation = fromStation.substring(0, fromStation.length() - 1);
                        int travelTime = scanner.nextInt();
                        pathTask.addQuery(new NearbyQuery(fromStation, travelTime));
                        break;
                    default:
                        return null;
                }
            }
        } catch (Exception e) {
            return null; // ignore - no logging available
        }
        return pathTask;
    }
}