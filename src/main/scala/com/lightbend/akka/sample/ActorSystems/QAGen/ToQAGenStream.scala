package com.lightbend.akka.sample.ActorSystems.QAGen

import akka.actor.{Actor, ActorRef, Props}
import com.lightbend.akka.sample.ActorSystems.QAGen.model.Sentence

object ToQAGenStream {

  def props(dest:ActorRef): Props = Props(new ToQAGenStream(dest))
}
class ToQAGenStream(dest:ActorRef) extends Actor {

  def receive: Receive = {
    case msg: String =>
     // println(s"At ToQAGenStream with $msg")
      dest ! msg
  }
}
