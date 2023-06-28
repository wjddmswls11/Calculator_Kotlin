package com.example.calculator_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.calculator_kotlin.databinding.ActivityMainBinding
import net.objecthunter.exp4j.ExpressionBuilder
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    //마지막으로 입력된 값이 숫자인지 아닌지를 판단하기 위한 변수
    var lastNumberic = false
    //유효하지 않은 경우에 true로 설정되어 에러를 처리하기 위해 사용
    var stateError = false
    //소수점을 입력한 여부를 저장하기 위해 사용
    var lastDot = false
    //백분율 기호가 입력되었는지 여부를 추적하는 데 사용
    var lastPercent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onAllclearClick(view: View) {
        binding.txtExpression.text = ""
        binding.txtResult.text = ""
        stateError = false
        lastDot = false
        lastNumberic = false
        binding.txtResult.visibility = View.GONE
    }

    //숫자와 .을 클릭 했을 경우 호출
    fun onDigitClick(view: View) {
        //stateError가 true인 경우 이전에 계산을 수행하는 과정에서 에러가 발생한 상태이므로,
        //표현식 창에 눌린 버튼의 값을 설정하고, stateError를 false로 변경
        if (stateError) {
            if (view is TextView){
                binding.txtExpression.text = view.text
            }
            stateError = false
        } else {
            if (view is TextView){
                binding.txtExpression.append(view.text)
            }
        }

        lastPercent = false
        //%기호가 마지막으로 입력되었는지를 추적하는 변수
        //만약 %기호가 마지막으로 입력된 후에 숫자난 '.'기호가 입력된다면, 이전에 입력된 %기호는 무시하고 숫자난 '.'기호를
        //처리해야 합니다. 그래서 %기호 이후에 숫자나 .기호가 입력된 경우 lastpercent변수를 false로 변경하여 %기호가 마지막으로
        //입력된 것이 아니라는 것을 알려주는 것입니다.

        //계산 결과를 바로 보여줄 수 있도록 onEqual() 함수를 호출합니다.
        onEqual()

        //소수점 처리
        if (view is TextView) {
            if (view.text == "."){
                if (!lastDot){
                    binding.txtExpression.append(".")
                    lastDot = true
                    lastNumberic = false //소수점이 입력된 경우, lastNumberic 변수를 갱신하지 않음
                }
            }else {
                lastDot = false
                lastNumberic = true //소수점이 아닌 숫자가 입력된 경우, lastNumberic 변수를 true로 갱신
            }
        }
    }

    fun onPercentClick(view: View) {
        if (lastNumberic && !lastPercent) {
            val expressionStr = binding.txtExpression.text.toString()

            // 연산자 이전 숫자에서 % 처리

            //expressionstr에서 연산자 중 가장 마지막에 위치한 연산자의 인덱스를 찾아내는 것
            val operators = charArrayOf('+','-','*','/','%')
            val lastOperatorIndex = expressionStr.indexOfLast { it in operators }

            val operandStr = expressionStr.substring(lastOperatorIndex + 1)
            val operandValue = operandStr.toDouble() / 100.0
            val newExpressionStr = expressionStr.substring(0, lastOperatorIndex + 1) + operandValue.toString()

            binding.txtExpression.setText(newExpressionStr)
            lastPercent = true
            lastNumberic = false
            onEqual()
        }
    }




    fun onOperatorClick(view: View) {

        if (!stateError && (lastNumberic || lastPercent)) {
            //백분율 처리를 먼저 합니다.
            if (lastPercent) {
                onPercentClick(view)
            }

            binding.txtExpression.append((view as Button).text)
            lastDot = false
            lastNumberic = false
            lastPercent = false

            //계산 결과를 바로 보여줄 수 있도록 onEqual()함수를 호출합니다.
            onEqual()
        }
    }


    fun onEqual() {
        if (lastNumberic && !stateError) {
            var expressionStr = binding.txtExpression.text.toString()
                .replace("×", "*")
                .replace('÷', '/')
                .replace("%*", "%·")
                .replace("X", "*")

            //백분율 연산자 처리
            expressionStr = expressionStr.replace("%","/100")


//            if (lastPercent) {  //백분율 연산자 처리
//                val operandStart = expressionStr.lastIndexOfAny(charArrayOf('+','-','*','/'))+1
//                if (operandStart < expressionStr.length) {
//                    expressionStr = expressionStr.substring(0, operandStart) + (expressionStr.substring(operandStart).toDouble()/100).toString()
//                    lastPercent = false
//                }
//            }


            try {
                val expression = ExpressionBuilder(expressionStr).build()
                val result = expression.evaluate()
                binding.txtResult.visibility = View.VISIBLE

                //BigDecimal을 사용하여 정확한 실수 계산 처리
                val bd = BigDecimal(result).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
                binding.txtResult.text = "=$bd"
                binding.txtExpression.setText(binding.txtResult.text.substring(1))//결과값을 표시

                //계산 성공 후에 lastNumberic을 true로 설정
                lastNumberic = true

            } catch (ex: Exception) {
                Log.e("evaluate error", ex.toString())
                Toast.makeText(this, "Error: $ex", Toast.LENGTH_LONG).show()
                binding.txtResult.text = "Error"
                stateError = true
                lastNumberic = false
            }

        }
    }

    fun resultButtonClicked(view: View) {
        val result = binding.txtResult.text.toString()
        if (result.isNotEmpty()) {
            binding.txtExpression.text = result.substring(1) // "=" 제외한 문자열 옮기기
            binding.txtResult.text = ""
            binding.txtResult.visibility = View.GONE
        }
    }
}