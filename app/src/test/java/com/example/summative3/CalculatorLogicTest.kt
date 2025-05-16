import com.example.summative3.screens.CalculatorLogic
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculatorLogicTest {

    private lateinit var calculator: CalculatorLogic

    @Before
    fun setup() {
        calculator = CalculatorLogic()
    }

    @Test
    fun testAdd() {
        calculator.onDigit("5")
        calculator.onOperator("+")
        calculator.onDigit("3")
        calculator.onEquals()
        assertEquals("8.0", calculator.display)
    }

    @Test
    fun testClear() {
        calculator.onDigit("9")
        calculator.onClear()
        assertEquals("0", calculator.display)
    }

    @Test
    fun testDivideByZero() {
        calculator.onDigit("9")
        calculator.onOperator("÷")
        calculator.onDigit("0")
        calculator.onEquals()
        assertEquals("Error", calculator.display)
    }
}
