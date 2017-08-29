package com.lightbend.akka.sample.ActorSystems.QAGen

/**
  * Created by burnish on 28/08/17.
  */
import com.lightbend.akka.sample.ActorSystems.QAGen.model.{BatchedSentence, Sentence, SentenceQs, SentenceQsAs}
import model.Sentence._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait QAJsonProtocols extends DefaultJsonProtocol {
  implicit val sentenceFormat: RootJsonFormat[Sentence] = jsonFormat1(Sentence.apply)
  implicit val batchedSentenceFormat: RootJsonFormat[BatchedSentence] = jsonFormat1(BatchedSentence.apply)
  implicit val sentenceQs: RootJsonFormat[SentenceQs] = jsonFormat2(SentenceQs.apply)
  implicit val sentenceQsAs: RootJsonFormat[SentenceQsAs] = jsonFormat3(SentenceQsAs.apply)
}
