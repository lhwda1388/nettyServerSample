package com.sample.nss.http;

import org.springframework.core.io.ClassPathResource;

import com.sample.nss.StartUpUtil;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter @Setter
@AllArgsConstructor
public class HttpStaticServer {
	int port;
	
	public void run() throws Exception {
		final String index = System.getProperty("user.dir") + "/html/http/index.html";
		StartUpUtil.runServer(this.port, p -> {
			
			p.addLast(new HttpServerCodec());
			p.addLast(new HttpObjectAggregator(65536));
			p.addLast(new HttpStaticFileHandler("/", index));
			p.addLast(new HttpNotFoundHandler());
		});
		
	}
	
}
