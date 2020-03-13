package com.sample.nss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sample.nss.chat.ChatServer;
import com.sample.nss.echo.EchoServer;
import com.sample.nss.http.HttpStaticServer;
import com.sample.nss.ws.ProxyWebChatServer;
import com.sample.nss.ws.WebChatServer;

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
		// HttpStaticServer hss = new HttpStaticServer(8030);
		// hss.run();
		
		// 04: chat server
		// ChatServer cs = new ChatServer(8040);
		// cs.run();
		// 05: ws chat server
		// WebChatServer wcs = new WebChatServer(8050);
		// wcs.run();
		
		ProxyWebChatServer ps = new ProxyWebChatServer();
		ps.run();
	}

}
