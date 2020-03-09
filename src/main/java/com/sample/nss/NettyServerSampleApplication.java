package com.sample.nss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sample.nss.echo.EchoServer;
import com.sample.nss.http.HttpStaticServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

@SpringBootApplication
public class NettyServerSampleApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(NettyServerSampleApplication.class, args);
		// 01 : discard  server
		// DiscardServer ds = new DiscardServer(8010);
		// ds.run();
	
		// 02 : echo server
		// EchoServer es = new EchoServer(8020);
		// es.run();
		
		// 03: http server
		HttpStaticServer hss = new HttpStaticServer(8030);
		hss.run();
	}

}
