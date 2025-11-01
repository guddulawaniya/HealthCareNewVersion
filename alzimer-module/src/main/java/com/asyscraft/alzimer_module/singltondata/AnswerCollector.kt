package com.asyscraft.alzimer_module.singltondata

import com.careavatar.core_model.alzimer.AnswerRequest
import com.careavatar.core_model.alzimer.QuestionAnswerRequest

object AnswerCollector {

    private val _answers = mutableListOf<AnswerRequest>()
    val answers: List<AnswerRequest> get() = _answers

    fun addAnswer(answer: AnswerRequest) {
        _answers.add(answer)
    }

    fun clearAnswers() {
        _answers.clear()
    }

    fun getFinalRequest(): QuestionAnswerRequest {
        return QuestionAnswerRequest(answers)
    }
}