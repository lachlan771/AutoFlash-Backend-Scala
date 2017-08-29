package com.lightbend.akka.sample.ActorSystems.QAGen.model

/**
  * Created by burnish on 28/08/17.
  */
case class BatchedSentence(sentences: List[Sentence]) extends QAGenMessage
