//#full-example

package com.lightbend.akka.sample
import org.reactivestreams.Publisher
import akka.stream.OverflowStrategy.fail
import akka.stream.scaladsl.Source
import akka.stream._
import akka.stream.scaladsl._
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.util.ByteString

import scala.concurrent._
import SourceShape._

import scala.concurrent.duration._
import java.nio.file.Paths

import akka.NotUsed
import com.lightbend.akka.sample.ActorSystems.QAGen.model.BatchedSentence
//Json format imports
import com.lightbend.akka.sample.ActorSystems.QAGen.model.QAGenMessage
import com.lightbend.akka.sample.ActorSystems.QAGen.QAJsonProtocols

import scala.concurrent.duration._
import akka.stream.scaladsl._
import akka.stream._

import scala.concurrent.{Await, ExecutionContext, Future}
import akka.testkit.TestProbe
import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.config.ConfigFactory
import akka.actor.Actor
import akka.actor.Props
import akka.util.Timeout
import java.util.concurrent.atomic.AtomicInteger

import akka.stream.scaladsl.Flow
import akka.Done
import com.lightbend.akka.sample.ActorSystems.QAGen.QAGenChiefSupervisor
import com.lightbend.akka.sample.ActorSystems.QAGen.model.Sentence
//#greeter-companion
//#greeter-messages
/*
object Greeter {
  //#greeter-messages
  def props(message: String, printerActor: ActorRef): Props = Props(new Greeter(message, printerActor))
  //#greeter-messages
  final case class WhoToGreet(who: String)
  case object Greet
}
//#greeter-messages
//#greeter-companion

//#greeter-actor
class Greeter(message: String, printerActor: ActorRef) extends Actor {
  import Greeter._
  import Printer._

  var greeting = ""
  var testfunction =(i:String)=>s"$i testing"

  def receive = {
    case WhoToGreet(who) =>
      greeting = testfunction(message)
    case Greet           =>
      //#greeter-send-message
      printerActor ! Greeting(greeting)
      //#greeter-send-message
  }
}
//#greeter-actor

//#printer-companion
//#printer-messages
object Printer {
  //#printer-messages
  def props: Props = Props[Printer]
  //#printer-messages
  final case class Greeting(greeting: String)
}
//#printer-messages
//#printer-companion

//#printer-actor
class Printer extends Actor with ActorLogging {
  import Printer._

  def receive = {
    case Greeting(greeting) =>
      log.info(s"Greeting received (from ${sender()}): $greeting")
  }
}
//#printer-actor
//Worker actor
object TestWorker{
  def props(message:String,receiver:ActorRef): Props = Props(new TestWorker(message,receiver))
}
class TestWorker(message:String,receiver:ActorRef) extends Actor with ActorLogging{
  import TestWorker._
  receiver ! message
  def receive: Receive = ???
}
//Testing supervisor
object Test${
  case class Message(from:String)
  def props(receiver:ActorRef): Props = Props(new Test$(receiver))
}
class Test$(receiver:ActorRef) extends Actor with ActorLogging{
  import Test$._

  def receive = {
    case Message(message)=>
      log.info(s"the message $message was sent to be a worker")
      val child = context.actorOf(TestWorker.props(message,receiver), name = "myChild")

      //Creating a child

  }

}
*/
//Function for prematerialized source

//Akka Composite flow test
class MyActor(dest:ActorRef) extends Actor {
  val initMessage = "start"
  val onCompleteMessage = "done"
  val ackMessage = "ack"
  def receive: Receive = {
    case `initMessage`       =>  println("start")
    case `onCompleteMessage` => println("done!")
    case msg: Int            => dest ! msg
  }
}

class ActorWithBackPressure(ref: ActorRef,dest:ActorRef) extends Actor {
  val initMessage = "start"
  val onCompleteMessage = "done"
  val ackMessage = "ack"
  def receive = {
    case `initMessage`=>
      sender() ! ackMessage
      ref forward initMessage
    case `onCompleteMessage` =>
      ref forward onCompleteMessage
    case msg: Int =>
      sender() ! ackMessage
      ref forward msg
  }
}

//#main-class
object AkkaQuickstart extends App {
  val initMessage = "start"
  val onCompleteMessage = "done"
  val ackMessage = "ack"
  //import com.lightbend.akka.sample.ActorWithBackPressure
  //import com.lightbend.akka.sample.MyActor

  // Create the 'helloAkka' actor system
//  implicit val system = ActorSystem("helloAkka")
//  implicit val materializer = ActorMaterializer()
  implicit val system = ActorSystem("ActorWithBackPressure")
  implicit val materializer = ActorMaterializer()
  //Function for the prematerialized queue ref
  //T is the source type, here String
  //M is the materialization type, here a SourceQueue[String]
  //obtaining materialized value for actor ref while still being able to connect stream architecture in discrete stages
  // From here https://bartekkalinka.github.io/2017/02/12/Akka-streams-source-run-it-publish-it-then-run-it-again.html
  object RunWithHub {
       def source[A, M](normal: Source[A, M])(implicit fm: Materializer, system: ActorSystem): (Source[A, NotUsed], M) = {
          val (normalMat, hubSource) = normal.toMat(BroadcastHub.sink(bufferSize = 256))(Keep.both).run
          (hubSource, normalMat)
       }
  }


  //Testing Data
  val testSentence1 = Sentence("Test Sentence 1")
  val testSentence2 = Sentence("This is the second Test sentence")
  val batchedSentenceTest= BatchedSentence(List(testSentence1,testSentence2))
  val tests = QAGenMessage.toJson(testSentence1)
  val testData = List(QAGenMessage.toJson(batchedSentenceTest))
  //Creation of the QAGen Stream
  //Begin creation from the smallest parts to the biggest
  //1st is creating the components of the compositeflow
      //The receiver of messages from the actor system is created 1st
      val sourceTemp = Source.actorRef[String](1000, OverflowStrategy.fail)
      val (qANestedSource, streamEntry: ActorRef) = RunWithHub.source(sourceTemp)
      //The Main supervisor actor is created where it is given the destination of where to send the messages from the actor system
      val actor = system.actorOf(QAGenChiefSupervisor.props(streamEntry))
      //The sink is created that sends the messages to the actor system using a Actor with acknowledgment
      val qANestedSink = Sink.actorRefWithAck(actor,initMessage,ackMessage,onCompleteMessage)
      //The sink and source are then combined into qACompositeFlow
      val qACompositeFlow: Flow[String,String, NotUsed] = Flow.fromSinkAndSource(qANestedSink, qANestedSource)
  //2nd. A source is created that gets the messages from kafka
      val qAKafkaSource: Source[String, NotUsed] = Source(testData)
  //Finally the Source and composite flow are combined to create qAGenStream
      val QAGenStream: Source[String,NotUsed] = qAKafkaSource.via(qACompositeFlow)



  //Creating a runnable graph and testing the Stream
  val testRun: RunnableGraph[NotUsed] = QAGenStream.to(Sink.foreach[String](println(_)))
  testRun.run()


















/*
  } finally {
    system.terminate()
  }*/
}
//#main-class
//#full-example
