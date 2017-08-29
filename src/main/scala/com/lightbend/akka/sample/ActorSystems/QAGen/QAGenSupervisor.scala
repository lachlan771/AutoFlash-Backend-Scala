package com.lightbend.akka.sample.ActorSystems.QAGen

import akka.actor.{Actor, ActorRef, Props}
import com.lightbend.akka.sample.ActorSystems.QAGen.model.Sentence

/**
  * Created by burnish on 28/08/17.
  */
object QAGenSupervisor {

  def props(dest:ActorRef): Props = Props(new QAGenSupervisor(dest))
}
class QAGenSupervisor(dest:ActorRef) extends Actor {
  val toQAGenStream: ActorRef = context.actorOf(ToQAGenStream.props(dest), name = "ToQAGenStream")
  val aGen$: ActorRef = context.actorOf(AGenSupervisor.props(toQAGenStream), name = "AGen-Supervisor")
  val qGen$: ActorRef = context.actorOf(QGenSupervisor.props(aGen$), name = "QGen-Supervisor")
  println("new QAGen$")
  def receive: Receive = {
    case msg: String =>

      qGen$ ! msg
  }
}
