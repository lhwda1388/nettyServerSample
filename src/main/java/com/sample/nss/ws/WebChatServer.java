package com.sample.nss.ws;

import com.sample.nss.StartUpUtil;
import com.sample.nss.http.HttpNotFoundHandler;
import com.sample.nss.http.HttpStaticFileHandler;

import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class WebChatServer {
	private int port;
	
	public void run() throws Exception {
		final String index = System.getProperty("user.dir") + "/html/ws/index.html";
		StartUpUtil.runServer(this.port, p -> {
			p.addLast(new HttpServerCodec());
			p.addLast(new HttpObjectAggregator(65536));
			// 핸드쉐이크 처리
			p.addLast(new WebSocketHandshakeHandler("/chat", new WebChatHandler()));
			// html 파일처리
			p.addLast(new HttpStaticFileHandler("/", index));
			// 404처리
			p.addLast(new HttpNotFoundHandler());
		});
	}
}
