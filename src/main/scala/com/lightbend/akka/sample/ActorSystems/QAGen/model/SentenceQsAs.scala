package com.lightbend.akka.sample.ActorSystems.QAGen.model

/**
  * Created by burnish on 28/08/17.
  */
case class SentenceQsAs( sentence:String, questions: List[String], answers:List[String]) extends QAGenMessage
