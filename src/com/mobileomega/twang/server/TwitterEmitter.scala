package com.mobileomega.twang.server

import akka.actor.ActorSystem

object TwitterEmitter {
  
  def main(args: Array[String]): Unit = {
    // because you comment 
    
    if (args.isEmpty) {
      val system = ActorSystem("ClientAndServer")
      val serverAddress = new InetSocketAddress("127.0.0.1", 6000)
      server(system, serverAddress)
      client(system, serverAddress)
    } else {
      val serverAddress =
        if (args.length == 3) new InetSocketAddress(args(1), args(2).toInt)
        else new InetSocketAddress("127.0.0.1", 6000)
      if (args(0) == "server") {
        val system = ActorSystem("Server")
        server(system, serverAddress)
      } else if (args(0) == "client") {
        val system = ActorSystem("Client")
        client(system, serverAddress)
      }
    }
  }
  
}