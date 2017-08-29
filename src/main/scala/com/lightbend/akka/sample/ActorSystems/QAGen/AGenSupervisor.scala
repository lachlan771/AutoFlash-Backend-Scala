package com.lightbend.akka.sample.ActorSystems.QAGen

import akka.actor.{Actor, ActorRef, Props}
import com.lightbend.akka.sample.ActorSystems.QAGen.model.Sentence

/**
  * Created by burnish on 28/08/17.
  */
object AGenSupervisor {

  def props(dest:ActorRef): Props = Props(new AGenSupervisor(dest))
}
class AGenSupervisor(dest:ActorRef) extends Actor {

  def receive: Receive = {
    case msg: String =>
     // println(s"At AGen with $msg")
      context.actorOf(AGenActor.props(dest))!msg
  }
}