//#full-example
package com.lightbend.akka.sample
import akka.stream._
import akka.stream.scaladsl._
import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props }
import akka.{ NotUsed, Done }
import akka.actor.ActorSystem
import akka.util.ByteString
import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.Paths

//#greeter-companion
//#greeter-messages
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

//#main-class
object AkkaQuickstart extends App {
  import Greeter._

  // Create the 'helloAkka' actor system
  implicit val system = ActorSystem("helloAkka")
  implicit val materializer = ActorMaterializer()



  try {
    //#create-actors
    // Create the printer actor
    // val printer: ActorRef = system.actorOf(Printer.props, "printerActor")

    // // Create the 'greeter' actors
    // val howdyGreeter: ActorRef =
    //   system.actorOf(Greeter.props("Howdy", printer), "howdyGreeter")
    // val helloGreeter: ActorRef =
    //   system.actorOf(Greeter.props("Hello", printer), "helloGreeter")
    // val goodDayGreeter: ActorRef =
    //   system.actorOf(Greeter.props("Good day", printer), "goodDayGreeter")
    // //#create-actors

    // //#main-send-messages. ! is a tell
    // howdyGreeter ! WhoToGreet("Akka")
    // howdyGreeter ! Greet

    // howdyGreeter ! WhoToGreet("Lightbend")
    // howdyGreeter ! Greet

    // helloGreeter ! WhoToGreet("Scala")
    // helloGreeter ! Greet

    // goodDayGreeter ! WhoToGreet("Play")
    // goodDayGreeter ! Greet
    // //#main-send-messages

    // println(">>> Press ENTER to exit <<<")
    // StdIn.readLine()\
    //val source  = Source(1 to 60).map(_.toString)
    //source.runForeach(i => println(i))(materializer)
    // val QAGenConsumerSource: Source[String,NotUsed] = Source(1 to 60)
    // .map(_.toString)
    // .named("QAGen-ConsumerSource");
    // QAGenConsumerSource.runForeach(i=>println(i))(materializer)

    //First is the QASream which is a source getting information form kafka.In prototyping it gets it from a predefined source
        //It is a Source made up of three parts
        //The first part is a source that consumes from a kafka stream.
            //Input:A batch of sentences with user info(email(or uuid) and deck name (or uuid))
            //Output: Same as input
    val QAGenConsumerSource: Source[String,NotUsed] = Source(1 to 60)
      .map(_.toString)
      .named("QAGen-ConsumerSource");
    QAGenConsumerSource.runForeach(i=>println(i))(materializer)
    //The second part sends the SBatchUserInfo to the actor system. It is a sink
            //Input: A batch of sentences with user info(email(or uuid) and deck name (or uuid))
            //Output: No output
    val QAGenSenderSink: Sink[String,NotUsed] = Sink.foreach[String](println(_))
    //The third part recieves the messages from the actor system. It is a source
            //Input:No real input but it recieves messages from the QAActor system in the form: sentence, questions, answers, UserInfo:deckid, userid
            //Output: sentence, questions, answers, UserInfo:deckid, userid, all as strings

    val QAGenReceiverSource : Source[String,NotUsed] = Source.empty//runforeach( i=> println(i))(materializer)
    //Combine all the stages in two stages;
        //The first is putting the QAGenSenderSink and QAGenReceiverSource together
    val QANestedStream :Flow[String,String,NotUsed] = Flow.fromSinkAndSource(QAGenSenderSink,QAGenReceiverSource)
    //The second is combining them all into the QAGenStream Source
    val QAGenStream : Source[String,NotUsed] = QAGenStream.via(QANestedStream)
    //Testing sink to make QAGenStream runnable,
    val QAGenStreamTest: RunnableGraph[NotUsed] = QAGenStream.to(Sink.foreach[String](println(_)))
    //  QAGenStreamTest.run()

  } finally {
    system.terminate()
  }
}
//#main-class
//#full-example
