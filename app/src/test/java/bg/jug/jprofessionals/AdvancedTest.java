package bg.jug.jprofessionals;

import org.junit.jupiter.api.Test;

import static java.lang.StringTemplate.RAW;

public class AdvancedTest {
    // <editor-fold desc="RAW processor">
    void rawProcessor() {
        String name = "Joan";
        {
            String info = STR."My name is \{name}";
        }
        // is equivalent to
        {
            StringTemplate st = RAW."My name is \{name}";
            String info = STR.process(st);
        }
    }
    // </editor-fold>
    // <editor-fold desc="literal string with expressions are no longer allowed">
    void literalStringWithExpressionWithoutProcessor() {
//        String invalid = "\{input.x} plus \{input.y} is \{input.x + input.y}";
//        String block = """
//                \{input.x} plus \{input.y} is \{input.x + input.y}
//                """;
    }
    // </editor-fold>
}
