package com.example.summative3.screens

class CalculatorLogic {
    var display: String = "0"
        private set

    private var operand1: Double? = null
    private var operator: String? = null
    private var resetDisplay = false

    fun onDigit(digit: String) {
        display = if (resetDisplay || display == "0") digit else display + digit
        resetDisplay = false
    }

    fun onOperator(op: String) {
        operand1 = display.toDoubleOrNull()
        operator = op
        resetDisplay = true
    }

    fun onClear() {
        display = "0"
        operand1 = null
        operator = null
        resetDisplay = false
    }

    fun onEquals() {
        val operand2 = display.toDoubleOrNull()
        val result = when (operator) {
            "+" -> operand1?.plus(operand2 ?: 0.0)
            "-" -> operand1?.minus(operand2 ?: 0.0)
            "×" -> operand1?.times(operand2 ?: 0.0)
            "÷" -> if (operand2 == 0.0) null else operand1?.div(operand2 ?: 1.0)
            else -> null
        }
        display = result?.toString() ?: "Error"
        operand1 = null
        operator = null
        resetDisplay = true
    }
}
