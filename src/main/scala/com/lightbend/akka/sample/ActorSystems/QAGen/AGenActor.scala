package com.lightbend.akka.sample.ActorSystems.QAGen

import akka.actor.{Actor, ActorRef, Props}
import com.lightbend.akka.sample.ActorSystems.QAGen.model.{QAGenMessage, Sentence, SentenceQs, SentenceQsAs}
import spray.json._
/**
  * Created by burnish on 28/08/17.
  */
object AGenActor {

import akka.actor.ActorRef

def props(dest:ActorRef): Props = Props(new AGenActor(dest))
}
class AGenActor(dest:ActorRef) extends Actor{

  def onMessage(msg: String): Unit = {
    val sentenceQs = msg.parseJson.convertTo[SentenceQs]
    val sentenceQsAs: SentenceQsAs= SentenceQsAs(sentenceQs.sentence,sentenceQs.questions,List("Answer 1","Answer2"))
    dest ! QAGenMessage.toJson(sentenceQsAs)
  }

  def receive: Receive = {
    case msg: String =>
      //println(s"At AGenActor with $msg")

      onMessage(msg)
      //context stop self
  }
}