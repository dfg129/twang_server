package com.mobileomega.twang.server

import akka.io.IO
import akka.pattern.ask
import akka.actor.ActorSystem
import akka.stream.MaterializerSettings
import akka.stream.io.StreamTcp
import akka.stream.scaladsl2._
import akka.util.{ ByteString, Timeout }
import java.net.InetSocketAddress
import scala.concurrent.duration._

object TwitterEmitter {
  def main(args: Array[String]): Unit = {

    if (args.isEmpty) {
      val system = ActorSystem("ClientAndServer")
      val serverAddress = new InetSocketAddress("127.0.0.1", 6000)
      server(system, serverAddress)
      client(system, serverAddress)
    }

  }

  def server(system: ActorSystem, serverAddress: InetSocketAddress): Unit = {
    implicit val sys = system
    import system.dispatcher

    implicit val materializer = FlowMaterializer()
    implicit val timeout = Timeout(5.seconds)

    val serverFuture = IO(StreamTcp) ? StreamTcp.Bind(serverAddress)

    serverFuture.onSuccess {
      case serverBinding: StreamTcp.TcpServerBinding =>
        println("Server started, listening on: " + serverBinding.localAddress)

        Source(serverBinding.connectionStream).foreach { conn =>
          println("Client connected from: " + conn.remoteAddress)
        }
    }

    serverFuture.onFailure {
      case e: Throwable =>
        println(s"Server could not bind to $serverAddress: ${e.getMessage}")
        system.shutdown()
    }

  }
  
  def client(system: ActorSystem, serverAddress: InetSocketAddress): Unit = {
    implicit val sys = system
    import system.dispatcher
    implicit val materializer = FlowMaterializer()
    implicit val timeout = Timeout(5.seconds)
    
    val clientFuture = IO(StreamTcp) ? StreamTcp.Connect(serverAddress)
    clientFuture.onSuccess {
      case clientBinding: StreamTcp.OutgoingTcpConnection => 
        val testInput = ('a' to 'z').map(ByteString(_))
        Source(testInput).connect(Sink(clientBinding.outputStream)).run()
        
        Source(clientBinding.inputStream).fold(Vector.empty[Char]) { (acc, in) => acc ++ in.map(_.asInstanceOf[Char]) }.
           onComplete {
          case Susccess(result) => 
            println(s"Result:" + result.mkString("[", ", ", "]"))
            println("Shutting down the client")
            system.shutdown()
        }
          
        }
          
        }
    }
  }

}