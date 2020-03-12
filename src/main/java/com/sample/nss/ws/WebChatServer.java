package com.sample.nss.ws;

import com.sample.nss.StartUpUtil;

import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WebChatServer {
	private int port;
	
	public void run() throws Exception {
		StartUpUtil.runServer(this.port, p -> {
			p.addLast(new HttpServerCodec());
			p.addLast(new HttpObjectAggregator(65536));
			// 핸드쉐이크 처리
			// html 파일처리
			// 404처리
		});
	}
}
