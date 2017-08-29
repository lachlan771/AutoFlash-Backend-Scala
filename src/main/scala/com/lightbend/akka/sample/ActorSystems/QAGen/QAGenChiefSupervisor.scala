package com.lightbend.akka.sample.ActorSystems.QAGen

import akka.actor.{Actor, ActorRef, Props}
import com.lightbend.akka.sample.ActorSystems.QAGen.model.Sentence

/**
  * Created by burnish on 28/08/17.
  */
object QAGenChiefSupervisor{

  def props(dest:ActorRef): Props = Props(new QAGenChiefSupervisor(dest))
}
class QAGenChiefSupervisor(dest:ActorRef) extends Actor {
  val initMessage = "start"
  val onCompleteMessage = "done"
  val ackMessage = "ack"
  def receive: PartialFunction[Any, Unit] = {
    case `initMessage`=>
      sender() ! ackMessage
      //ref forward initMessage
    case `onCompleteMessage` =>
      //ref forward onCompleteMessage
    case msg: String =>
      sender() ! ackMessage
      //println(s"At QAGenChief Supervisor $msg")
      context.actorOf(QAGenSupervisor.props(dest))!msg

      //ref forward msg
  }
}