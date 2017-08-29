package com.lightbend.akka.sample.ActorSystems.QAGen.model

/**
  * Created by burnish on 28/08/17.
  */
case class SentenceQs(sentence: String, questions:List[String]) extends QAGenMessage