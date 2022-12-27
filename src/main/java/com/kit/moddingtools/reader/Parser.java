package com.kit.moddingtools.reader;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Parser {
  private JsonObject output;
  private String processed;

  public Parser(String path) {
    String[] raw = Reader.read(path);
    raw = sanitiseFile(raw);
    processed = convertSyntax(raw);
    output = toJsonObject(0, processed.length());
  }

  public JsonObject getOutput() {
    return output;
  }

  /**
   * Convert the file into a JSON object.
   * 
   * @param processed The file to be converted.
   * @param head The starting index of the data to be converted.
   * @param tail The ending index of the data to be converted.
   * @return The JSON object.
   */
  private JsonObject toJsonObject(int head, int tail) {
    JsonObject json = new JsonObject();

    int index = head;

    while (index < tail) {
      // Find the next key.
      int keyStart = findNextChar(index, '"');

      // Prevent reading the same object twice.
      if (keyStart == -1 || keyStart > tail) {
        break;
      }

      int keyEnd = findNextClosingChar(keyStart + 1, '"');
      String key = processed.substring(keyStart + 1, keyEnd);

      // Find the next value.
      int valueStart = findNextChar(keyEnd + 1, '"', '{', '[');
      char valueStartChar = processed.charAt(valueStart);
      int valueEnd = findNextClosingChar(valueStart + 1, valueStartChar);

      String value = processed.substring(valueStart + 1, valueEnd);

      switch (valueStartChar) {
        case '"':
          add(json, key, value);
          break;
        case '{':
          JsonObject object = toJsonObject(valueStart, valueEnd);
          add(json, key, object);
          break;
        case '[':
          // TODO: Add support for arrays.
          // add(json, key, toJsonArray(file, valueStart, valueEnd));
          break;
      }

      index = valueEnd + 1;
    }
    return json;
  }

  /**
   * Sanitise the file before parsing it into a JSON.
   * Leading and trailing whitespace, and comments are removed.
   * 
   * @param file The file to be processed.
   * @return The processed mod file.
   */
  public static String[] sanitiseFile(String[] file) {
    List<String> lines = new ArrayList<String>();

    for (String line : file) {
      // Regex to remove comments.
      line = line.replaceAll("#.*", "");

      // Trim whitespace.
      line = line.trim();

      // Ignore empty lines.
      if (!line.isEmpty()) {
        lines.add(line);
      }
    }

    return lines.toArray(new String[0]);
  }

  /**
   * Convert the file into JSON syntax.
   * 
   * @param file
   * @return
   */
  private static String convertSyntax(String[] file) {
    // Join all lines into one string.
    String joined = String.join("\n", file);

    // Replace " with '
    joined = joined.replaceAll("\"", "'");

    // Match any terms that are not whitespace, curly braces or equals signs.
    joined = joined.replaceAll("([^\\s{}=]+)", "\"$1\"");

    // Replace equals signs with colons.
    joined = joined.replaceAll("=", ":");

    // Replace "\n} with ",\n}
    joined = joined.replaceAll("\"\\n\"", "\",\n\"");

    // Replace }\n" with },\n"
    joined = joined.replaceAll("}\\n\"", "},\n\"");

    // Replace " " with ", "
    joined = joined.replaceAll("\" \"", "\", \"");

    // Replace \s:\s with :\s
    joined = joined.replaceAll("\\s:\\s", ": ");

    // Replace ,} with }
    joined = joined.replaceAll(",}", "}");

    // Remove newlines.
    joined = joined.replaceAll("\\n", "");

    return "{\n" + joined + "\n}";
  }

  /**
   * Find the next character in a string.
   * 
   * @param string The string to search.
   * @param start  The index to start searching from.
   * @param chars  The characters to search for.
   * @return The index of the next character.
   */
  private int findNextChar(int start, char... chars) {
    int index = start;

    while (index < processed.length()) {
      for (char c : chars) {
        if (processed.charAt(index) == c) {
          return index;
        }
      }
      index++;
    }

    return -1;
  }

  /**
   * Find the next closing character in a string.
   * 
   * @param string The string to search.
   * @param start  The index to start searching from.
   * @param c      The character to search for.
   * @return The index of the next closing character.
   */
  private int findNextClosingChar(int start, char c) {
    int index = start;
    int count = 0;

    while (index < processed.length()) {
      if (processed.charAt(index) == c) {
        if (c == '"') {
          return index;
        }

        count++;
      } else if (processed.charAt(index) == getClosingChar(c)) {
        if (count == 0) {
          return index;
        } else {
          count--;
        }
      }
      index++;
    }

    throw new RuntimeException("Could not find next closing character.");
  }

  /**
   * Get the closing character for a given character.
   * 
   * @param c The character to find the closing character for.
   * @return The closing character.
   */
  private static char getClosingChar(char c) {
    switch (c) {
      case '{':
        return '}';
      case '[':
        return ']';
      case '"':
        return '"';
      default:
        throw new RuntimeException("Invalid character.");
    }
  }

  /**
   * Add a property/ memeber to a JSON object. If key already exists, the value is
   * added to an array.
   * 
   * @param json  The JSON object to add the property to.
   * @param key   The key of the property.
   * @param value The value of the property.
   */
  private static void add(JsonObject json, String key, Object value) {
    if (json.has(key)) {
      if (json.get(key).isJsonArray()) {
        // If the key already exists, add the value to an array.
        addToJsonArray(json.getAsJsonArray(key), value);
      } else {
        // If the key already exists, but is not an array, convert it to an array.
        JsonArray array = new JsonArray();
        array.add(json.get(key));
        json.add(key, array);
        addToJsonArray(json.getAsJsonArray(key), value);
      }
    } else {
      if (value instanceof String) {
        json.addProperty(key, (String) value);
      } else if (value instanceof JsonObject) {
        json.add(key, (JsonObject) value);
      }
    }
  }

  /**
   * Add a value to a JSON array.
   * 
   * @param array The array to add the value to.
   * @param value The value to add.
   */
  private static void addToJsonArray(JsonArray array, Object value) {
    if (value instanceof String) {
      array.add((String) value);
    } else if (value instanceof JsonObject) {
      array.add((JsonObject) value);
    }
  }
}