import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CalcTest {

    @Test
    void whenSum3And4Then7() {
        int actual = 7;
        int expected = Calc.sum(3, 4);
        assertThat(expected).isEqualTo(actual);
    }
}