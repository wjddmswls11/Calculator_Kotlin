package com.example.calculator_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.calculator_kotlin.databinding.ActivityMainBinding
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    var lastNumberic = false
    var stateError = false
    var lastDot = false

    private lateinit var expression: Expression

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


    fun onEqualClick(view: View) {

        onEqual()
        binding.txtExpression.text = binding.txtResult.text.toString().drop(1)


    }


    fun onDigitClick(view: View) {

        if(stateError){
            binding.txtExpression.text = (view as Button).text
            stateError = false
        }else {
            binding.txtExpression.append((view as Button).text)
        }

        lastNumberic = true
        onEqual()

    }



    fun onOperatorClick(view: View) {

        if (!stateError && lastNumberic){

            binding.txtExpression.append((view as Button).text)
            lastDot = false
            lastNumberic = false
            onEqual()

        }


    }


    fun onBackClick(view: View) {

        binding.txtExpression.text = binding.txtExpression.text.toString().drop(1)

        try {

            val lastChar = binding.txtExpression.text.toString().last()

            if (lastChar.isDigit()){
                onEqual()
            }
        }catch (e : Exception) {

            binding.txtResult.text = ""
            binding.txtResult.visibility = View.GONE
            Log.e("last char error",e.toString())

        }


    }


    fun onClearClick(view: View) {

        binding.txtExpression.text = ""
        lastNumberic = false


    }

    fun onEqual(){
        if (lastNumberic && !stateError){
            val txt = binding.txtExpression.text.toString()

            expression = ExpressionBuilder(txt).build()

            try {

                val result = expression.evaluate()

                binding.txtResult.visibility = View.VISIBLE

                binding.txtResult.text = "=" + result.toString()



            }catch (ex : ArithmeticException){
                Log.e("evaluate error", ex.toString())
                binding.txtResult.text = "Error"
                stateError = true
                lastNumberic = false
            }

        }

    }
}