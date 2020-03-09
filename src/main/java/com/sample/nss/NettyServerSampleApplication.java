package com.sample.nss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sample.nss.echo.EchoServer;

@SpringBootApplication
public class NettyServerSampleApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(NettyServerSampleApplication.class, args);
		// 01 : discard  server
		// DiscardServer ds = new DiscardServer(8010);
		// ds.run();
	
		// 02 : echo server
		EchoServer es = new EchoServer(8020);
		es.run();
	}

}
