package com.rom.routing;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;

/**
 * @author Roman Katerinenko
 */
public class RoutingTest {
    private ByteArrayOutputStream outputStream;

    @Test
    public void checkFormatting() throws UnsupportedEncodingException {
        String input = "8\n" +
                "A -> B: 240\n" +
                "A -> C: 70\n" +
                "A -> D: 120\n" +
                "C -> B: 60\n" +
                "D -> E: 480\n" +
                "C -> E: 240\n" +
                "B -> E: 210\n" +
                "E -> A: 300\n" +
                "path A -> B\n" +
                "near A, 130\n";
        String expected = "A -> C -> B: 130\n" +
                "C: 70, D: 120, B: 130";
        testRoutingOn(input, expected);
        input = "10\n" +
                "S -> T: 10\n" +
                "S -> Y: 5\n" +
                "T -> Y: 2\n" +
                "T -> X: 1\n" +
                "X -> Z: 4\n" +
                "Z -> X: 6\n" +
                "Z -> S: 7\n" +
                "Y -> T: 3\n" +
                "Y -> X: 9\n" +
                "Y -> Z: 2\n" +
                "path S -> X\n" +
                "path S -> Z\n" +
                "near S, 10000"; // all stations are reachable
        expected = "S -> Y -> T -> X: 9\n" +
                "S -> Y -> Z: 7\n" +
                "Y: 5, Z: 7, T: 8, X: 9";
        testRoutingOn(input, expected);
    }

    @Test
    public void checkPathsWithTheSameMinTimeS() throws UnsupportedEncodingException {
        String input = "5\n" +
                "A -> B: 6\n" +
                "B -> C: 4\n" +
                "B -> D: 4\n" +
                "C -> Z: 1\n" +
                "D -> Z: 1\n" +
                "path A -> Z\n" +
                "near A, 10000"; // all stations are reachable
        String expected = "A -> B -> C -> Z: 11\n" +
                "B: 6, C: 10, D: 10, Z: 11";
        testRoutingOn(input, expected);
    }

    @Test
    public void checkIfEqualEdgesGivenThenOnlyFirstIsUsed() throws UnsupportedEncodingException {
        String input = "4\n" +
                "A -> B: 6\n" +
                "A -> B: 7\n" +
                "A -> B: 8\n" +
                "A -> C: 10\n" +
                "path A -> B\n" +
                "near A, 10000"; // all stations are reachable
        String expected = "A -> B: 6\n" +
                "B: 6, C: 10";
        testRoutingOn(input, expected);
    }

    @Test
    public void checkErrorMessageIsWrittenWhenNopath() throws UnsupportedEncodingException {
        String input = "7\n" +
                "A -> B: 240\n" +
                "A -> C: 70\n" +
                "A -> D: 120\n" +
                "C -> B: 60\n" +
                "D -> E: 480\n" +
                "C -> E: 240\n" +
                "B -> E: 210\n" +
                "path E -> A\n" +
                "near E, 10000\n";
        String expected = "Error: No path from E to A\n" +
                "Error: No near stations E within 10000 sec.";
        testRoutingOn(input, expected);
    }

    @Test
    public void checkMixOfWrongAndCorrectQueriesHandledCorrectly() throws UnsupportedEncodingException {
        String input = "8\n" +
                "A -> B: 240\n" +
                "A -> C: 70\n" +
                "A -> D: 120\n" +
                "C -> B: 60\n" +
                "D -> E: 480\n" +
                "C -> E: 240\n" +
                "B -> E: 210\n" +
                "E -> A: 300\n" +
                "path A -> C\n" +
                "near Z, 130\n" + // wrong station 'Z'
                "near C, 900\n" +
                "path a -> B\n" +  // wrong station 'a'
                "path E -> A\n" +
                "path A -> b\n"; // wrong station 'b'
        String expected = "A -> C: 70\n" +
                "Error: No near stations Z within 130 sec.\n" +
                "B: 60, E: 240, A: 540, D: 660\n" +
                "Error: No path from a to B\n" +
                "E -> A: 300\n" +
                "Error: No path from A to b";
        testRoutingOn(input, expected);
    }

    @Test
    public void checkCornerCases() throws UnsupportedEncodingException {
        String input = "1\n" +
                "A -> B: 10\n" +
                "path A -> B\n" +
                "path B -> A\n" + // no path from A to B
                "near A, 9\n" + // no near stations within 9 sec.
                "near A, 10\n" + // B is near
                "near A, 0\n" + // no near stations within 0 sec.
                "near B, 10\n"; // no near stations at all
        String expected = "A -> B: 10\n" +
                "Error: No path from B to A\n" +
                "Error: No near stations A within 9 sec.\n" +
                "B: 10\n" +
                "Error: No near stations A within 0 sec.\n" +
                "Error: No near stations B within 10 sec.";
        testRoutingOn(input, expected);
        input = "4\n" +
                "A -> B: 0\n" +
                "B -> C: 0\n" +
                "C -> D: 0\n" +
                "D -> A: 0\n" +
                "path A -> A\n" + // same vertex path should be A->A
                "path A -> D\n" +
                "near B, 0\n";
        expected = "A -> A: 0\n" +
                "A -> B -> C -> D: 0\n" +
                "A: 0, C: 0, D: 0";
        testRoutingOn(input, expected);
    }

    @Test
    public void checkSeveralConnectedComponents() throws UnsupportedEncodingException {
        String input = "3\n" +
                "A -> B: 7\n" +
                "B -> C: 5\n" +
                "D -> E: 10\n" +
                "path A -> C\n" +
                "path A -> D\n" +
                "path E -> A\n" +
                "near A, 1000\n";
        String expected = "A -> B -> C: 12\n" +
                "Error: No path from A to D\n" +
                "Error: No path from E to A\n" +
                "B: 7, C: 12";
        testRoutingOn(input, expected);
    }

    @Test
    public void checkSameVertexPathIsZero() throws UnsupportedEncodingException {
        String input = "2\n" +
                "A -> B: 5\n" +
                "B -> A: 10\n" +
                "path A -> A\n";
        String expected = "A -> A: 0";
        testRoutingOn(input, expected);
        input = "2\n" +
                "A -> B: 0\n" +
                "B -> A: 0\n" +
                "path A -> A\n";
        expected = "A -> A: 0";
        testRoutingOn(input, expected);
        input = "1\n" +
                "A -> B: 0\n" +
                "path A -> A\n";
        expected = "A -> A: 0";
        testRoutingOn(input, expected);
        input = "1\n" +
                "A -> B: 0\n" +
                "path B -> B\n"; // still should be possible despite the fact that there is no edges from B
        expected = "B -> B: 0";
        testRoutingOn(input, expected);
    }

    @Test
    public void checkIncorrectInput() throws UnsupportedEncodingException {
        String input = "1\n" +
                "A -> B: 5\n" +
                "B -> A: 10\n" +
                "path A -> A\n";
        String expected = "Error: wrong input";
        testRoutingOn(input, expected);
        input = "1000\n" +
                "A -> B: 5\n" +
                "B -> A: 10\n" +
                "path A -> A\n";
        expected = "Error: wrong input";
        testRoutingOn(input, expected);
        input = "1000\n" +
                "A            -> B: 5\n" +
                "B -> A: 10\n" +
                "pathasdf A -> A\n";
        expected = "Error: wrong input";
        testRoutingOn(input, expected);
        input = "0\n" +
                "A -> B: 5\n" +
                "B -> A: 10\n" +
                "path A -> A\n";
        expected = "Error: wrong input";
        testRoutingOn(input, expected);
        input = "-100\n" +
                "A -> B: 5\n" +
                "B -> A: 10\n" +
                "path A -> A\n";
        expected = "Error: wrong input";
        testRoutingOn(input, expected);
        input = "   ";
        expected = "Error: wrong input";
        testRoutingOn(input, expected);
        input = "1\n" +  // no graph definition
                "path A -> A\n";
        expected = "Error: wrong input";
        testRoutingOn(input, expected);
        input = "2\n" + // no query definition
                "A -> B: 5\n" +
                "B -> A: 10\n";
        expected = "Error: wrong input";
        testRoutingOn(input, expected);
    }

    @Test
    public void checkNonAsciiChars() throws UnsupportedEncodingException {
        String input = "6\n" +
                "AmrumerStraße -> BerlinGörlitzerBahnhof: 10\n" +
                "BerlinGörlitzerBahnhof -> BernauerStraße: 2\n" +
                "BernauerStraße -> BetriebsbahnhofSchöneweide: 14\n" +
                "BetriebsbahnhofSchöneweide -> Grünau: 7\n" +
                "Grünau -> AmrumerStraße: 9\n" +
                "Grünau -> BernauerStraße: 3\n" +
                "near Grünau, 1000\n" +
                "path Grünau -> BetriebsbahnhofSchöneweide\n" +
                "path Grünau -> BerlinGörlitzerBahnhof\n";
        String expected = "BernauerStraße: 3, AmrumerStraße: 9, BetriebsbahnhofSchöneweide: 17, BerlinGörlitzerBahnhof: 19\n" +
                "Grünau -> BernauerStraße -> BetriebsbahnhofSchöneweide: 17\n" +
                "Grünau -> AmrumerStraße -> BerlinGörlitzerBahnhof: 19";
        testRoutingOn(input, expected);
    }

    @Test
    public void checkAlphaNumericStationNames() throws UnsupportedEncodingException {
        String input = "3\n" +
                "A1 -> B2: 10\n" +
                "B2 -> 3c3: 20\n" +
                "3c3 -> A1: 5\n" +
                "near 3c3, 100\n" +
                "near 3c3, 5\n" +
                "path 3c3 -> A1\n";
        String expected = "A1: 5, B2: 15\n" +
                "A1: 5\n" +
                "3c3 -> A1: 5";
        testRoutingOn(input, expected);
    }

    private void testRoutingOn(String actual, String expected) throws UnsupportedEncodingException {
        RoutingService.findPath(newInputStreamFor(actual), newOutputStream());
        assertActualEqualTo(expected);
    }

    private InputStream newInputStreamFor(String string) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(string.getBytes(RoutingService.CHARSET));
    }

    private OutputStream newOutputStream() throws UnsupportedEncodingException {
        return outputStream = new ByteArrayOutputStream();
    }

    private void assertActualEqualTo(String expected) throws UnsupportedEncodingException {
        String actualOutput = new String(outputStream.toByteArray(), RoutingService.CHARSET);
        assertEquals(expected, actualOutput);
    }
}