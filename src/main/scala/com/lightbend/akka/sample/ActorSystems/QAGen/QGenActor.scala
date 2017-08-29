package com.lightbend.akka.sample.ActorSystems.QAGen
import spray.json._
import akka.actor.{Actor, ActorRef, Props}
import com.lightbend.akka.sample.ActorSystems.QAGen.model.{QAGenMessage, Sentence, SentenceQs}

/**
  * Created by burnish on 28/08/17.
  */
object QGenActor {

  def props(dest:ActorRef): Props = Props(new QGenActor(dest))
}
class QGenActor(dest:ActorRef) extends Actor {

  def onMessage(msg: String): Unit = {
    val sentence = msg.parseJson.convertTo[Sentence]
    val sentenceQs:SentenceQs = SentenceQs(sentence.sentence,List("question 1","question 2"))
    dest ! QAGenMessage.toJson(sentenceQs)
  }

  def receive: Receive = {
    case msg: String =>
      //println(s"At QGenActor with $msg")
      onMessage(msg)
      //dest ! onMessage
      //context stop self
  }
}