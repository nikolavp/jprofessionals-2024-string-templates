/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package bg.jug.jprofessionals;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasicSTRProcessorTest {

    record TestRecord(int x, int y) { }
    private final TestRecord input = new TestRecord(1, 2);

    // <editor-fold desc="x plus y">
    @Test
    void xPlusY() {
        String result = STR."\{input.x} plus \{input.y} is \{input.x + input.y}";
        assertEquals(result, "1 plus 2 is 3");
    }
    // </editor-fold>
    // <editor-fold desc="x plus y with a method call">
    @Test
    void xPlusYWithMethodCalls() {
        TestRecord testRecord = new TestRecord(1, 2);
        String result = STR."\{testRecord.x} plus \{testRecord.y} is \{add(testRecord.x, testRecord.y)}";
        assertEquals(result, "1 plus 2 is 3");
    }

    int add(int first, int second) {
        return first + second;
    }
    // </editor-fold>
    // <editor-fold desc="x plus y multiline">
    @Test
    void complexExpressions_multiLine() {
        TestRecord testRecord = new TestRecord(1, 2);
        String result = STR."\{testRecord.x} plus \{testRecord.y} is \{
                add(testRecord.x, testRecord.y)
        }";
        assertEquals(result, "1 plus 2 is 3");
    }
    // </editor-fold>
    // <editor-fold desc="text blocks">
    @Test
    void multiLineStrings() {
        String title = "My Web Page";
        String text  = "Some text";
        String html = STR."""
        <html>
          <head>
            <title>\{title}</title>
          </head>
          <body>
            <p>\{text}</p>
          </body>
        </html>
        """;

        assertEquals(html, """
        <html>
          <head>
            <title>My Web Page</title>
          </head>
          <body>
            <p>Some text</p>
          </body>
        </html>
        """);
    }

    // </editor-fold>
}
