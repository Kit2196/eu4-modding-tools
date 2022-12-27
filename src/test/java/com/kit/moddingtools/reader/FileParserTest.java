package com.kit.moddingtools.reader;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for FileParser.
 */
public class FileParserTest {
  /**
   * Empty lines are ignored.
   */
  @Test
  public void testEmptyLines() {
    String[] lines = new String[] {
      "line1",
      "",
      "line2"
    };

    String[] expected = new String[] {
      "line1",
      "line2"
    };

    String[] actual = Parser.sanitiseFile(lines);

    verifyResult(expected, actual);
  }

  /**
   * Leading and trailing whitespace is removed.
   */
  @Test
  public void testWhitespace() {
    String[] lines = new String[] {
      " line1 ",
      " line2 "
    };

    String[] expected = new String[] {
      "line1",
      "line2"
    };

    String[] actual = Parser.sanitiseFile(lines);

    verifyResult(expected, actual);
  }

  /**
   * Comments are removed.
   */
  @Test
  public void testComments() {
    String[] lines = new String[] {
      "line1 # comment",
      "line2 # comment",
      "  # comment line 3"
    };

    String[] expected = new String[] {
      "line1",
      "line2"
    };

    String[] actual = Parser.sanitiseFile(lines);

    verifyResult(expected, actual);
  }

  private static void verifyResult(String[] expected, String[] actual) {
    int expectedLength = expected.length;
    int actualLength = actual.length;

    assertTrue("Expected length is " + expectedLength + " but actual length is " + actualLength, expectedLength == actualLength);

    for(int i = 0; i < expectedLength; i++) {
      assertTrue("Strings at index " + i + " are not equal, expected: " + expected[i] + " but actual: " + actual[i], expected[i].equals(actual[i]));
    }
  }
}
