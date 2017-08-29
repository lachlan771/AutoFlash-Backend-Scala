package com.lightbend.akka.sample.ActorSystems.QAGen

import akka.actor.{Actor, ActorRef, Props}
import com.lightbend.akka.sample.ActorSystems.QAGen.model.{BatchedSentence, QAGenMessage, Sentence}
import com.lightbend.akka.sample.ActorSystems.QAGen.QAJsonProtocols
import spray.json._

/**
  * Created by burnish on 28/08/17.
  */
object QGenSupervisor {

  def props(dest:ActorRef): Props = Props(new QGenSupervisor(dest))
}
class QGenSupervisor(dest:ActorRef) extends Actor {
  //val dest:ActorRef
  def createActor(msg:String,dest:ActorRef): Unit = {
    context.actorOf(QGenActor.props(dest))!msg
  }

  def splitBatch(msg:String) : Unit = {
    val bSentences = msg.parseJson.convertTo[BatchedSentence]
     for(x <-bSentences.sentences){
      createActor(QAGenMessage.toJson(x),dest)
    }
  }

  def receive: Receive = {
    case msg: String =>
      //println(s"At QGen with $msg")
      splitBatch(msg)
  }
}
