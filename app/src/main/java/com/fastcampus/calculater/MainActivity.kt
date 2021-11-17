package com.fastcampus.calculater

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text
import java.lang.NumberFormatException

class MainActivity : AppCompatActivity() {

    //계산기 조건
    //1. 연산자는 두 번 누를 수 없고
    //2. 숫자는 최대 15자리까지만

    private val expressionTextView: TextView by lazy {
        findViewById<TextView>(R.id.expressionTextView)
    }

    private val resultTextView: TextView by lazy {
        findViewById<TextView>(R.id.resultTextView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private var isOperator = false
    private var hasOperator = false

    //누르면 string값을 화면에 보여주는 함수
    fun buttonClicked(v: View) {
        when (v.id) {
            R.id.button0 -> numberButtonClicked("0")
            R.id.button1 -> numberButtonClicked("1")
            R.id.button2 -> numberButtonClicked("2")
            R.id.button3 -> numberButtonClicked("3")
            R.id.button4 -> numberButtonClicked("4")
            R.id.button5 -> numberButtonClicked("5")
            R.id.button6 -> numberButtonClicked("6")
            R.id.button7 -> numberButtonClicked("7")
            R.id.button8 -> numberButtonClicked("8")
            R.id.button9 -> numberButtonClicked("9")

            R.id.buttonPlus -> operatorButtonClicked("+")
            R.id.buttonMinus -> operatorButtonClicked("-")
            R.id.buttonMulti -> operatorButtonClicked("*")
            R.id.buttonDivider -> operatorButtonClicked("/")
            R.id.buttonModulo -> operatorButtonClicked("%")
        }
    }

    private fun numberButtonClicked(number: String) {
        //조건
        //숫자는 수자끼리 뭉쳐놓고 연산자를 쓸 경우는 한 칸 띄고 연산자 한 칸 띄고 숫자 이렇게 배치 할거임
        //split으로 구분할건데 스페이스로 구분 하도록 할 예정임

        if (isOperator) { //연산자를 클릭 후 들어왔을 경우 띄어쓰기 함
            expressionTextView.append(" ")
        }

        isOperator = false

        val expressionText = expressionTextView.text.split(" ")

        if (expressionText.isNotEmpty() && expressionText.last().length >= 15) {
            Toast.makeText(this, "15자리 까지만 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
            return
        } else if (expressionText.last().isEmpty() && number == "0") {
            Toast.makeText(this, "0은 제일 앞에 올 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        expressionTextView.append(number)
        //Todo resultTextView에 실시간으로 계산 결과를 넣어야 하는 기능

        resultTextView.text = calculateExpression()

    }

    private fun operatorButtonClicked(operator: String) {
        if(expressionTextView.text.isEmpty()) {//빈 칸일 경우 안눌리게
            return
        }

        when {
            isOperator -> { //연산자를 눌렀는데 다른 연산자를 바꾸고 싶은 경우
                //전에 눌렀던 연산자를 지우고(drop) 새로 씀
                val text = expressionTextView.text.toString()
                expressionTextView.text = text.dropLast(1) + operator//맨 끝에서부터 1자리만 지워줌
            }

            hasOperator -> { //연산자를 누르고 다시 연산자를 눌렀을 경우
                Toast.makeText(this, "연산자는 한 번만 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
                return
            }

            else -> {
                expressionTextView.append(" ${operator}")
            }

        }

        //연산자 색 바꾸는 코드
        val ssb = SpannableStringBuilder(expressionTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.green)),
            expressionTextView.text.length -1,
            expressionTextView.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        expressionTextView.text = ssb

        isOperator = true
        hasOperator = true

    }


    fun resultButtonClicked(v: View) {
        val expressionTexts = expressionTextView.text.split(" ")

        if(expressionTextView.text.isEmpty() || expressionTexts.size == 1) {
            return
        }

        if(expressionTexts.size !=3 && hasOperator) {
            Toast.makeText(this, "아직 완성되지 않은 수식입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not() ) {
            Toast.makeText(this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val expressionText = expressionTextView.text.toString()
        val resultText = calculateExpression()

        resultTextView.text = ""
        expressionTextView.text = resultText

        isOperator = false
        hasOperator = false

    }

    //ExpressionTextView에서 숫자와 연산자가 있는 값의 결과를 result에 반환 해 넣게 할 함수
    private fun calculateExpression(): String {
        val expressionTexts = expressionTextView.text.split(" ")

        if(hasOperator.not() || expressionTexts.size != 3) { //not==없으면, 아니면~
            return ""
        }
        else if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not() ) {
            return ""
        }

        val exp1 = expressionTexts[0].toBigInteger()
        val exp2 = expressionTexts[2].toBigInteger()
        val op = expressionTexts[1]

        return when(op) {
            "+" -> (exp1 + exp2).toString()
                "-" -> (exp1 - exp2).toString()
                "x" -> (exp1 * exp2).toString()
                "/" -> (exp1 / exp2).toString()
                "%" -> (exp1 % exp2).toString()
                else -> ""
        }

    }


    fun historyButtonClicked(v: View) {
        //
    }

    fun clearButtonClicked(v: View) {
        expressionTextView.text = ""
        resultTextView.text = ""
        isOperator = false
        hasOperator = false
    }
}

fun String.isNumber(): Boolean {
    return try {
        this.toBigInteger()
        true
    } catch (e: NumberFormatException) {
        false
    }
}
