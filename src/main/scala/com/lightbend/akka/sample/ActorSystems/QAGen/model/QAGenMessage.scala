package com.lightbend.akka.sample.ActorSystems.QAGen.model

import com.lightbend.akka.sample.ActorSystems.QAGen.QAJsonProtocols

/**
  * Created by burnish on 28/08/17.
  */
//import pl.bka.JsonProtocols

trait QAGenMessage

object QAGenMessage extends QAJsonProtocols {

  import spray.json._

  def toJson(msg: QAGenMessage): String = msg match {
    case message: Sentence => message.toJson.toString
    case message: BatchedSentence=>message.toJson.toString
    case message: SentenceQs=>message.toJson.toString
    case message: SentenceQsAs=>message.toJson.toString

  }
}