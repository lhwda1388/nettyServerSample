package com.sample.nss.chat;

import com.sample.nss.StartUpUtil;

import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ChatServer {
	int port;
	
	public void run() throws Exception {
		StartUpUtil.runServer(this.port, p -> {
			// p.addLast(new LineBasedFrameDecoder(1024, true, true));
			p.addLast(new StringDecoder(CharsetUtil.UTF_8), new StringEncoder(CharsetUtil.UTF_8));
			p.addLast(new ChatMessageCodec(), new LoggingHandler(LogLevel.DEBUG));
			p.addLast(new ChatServerHandler(), new LoggingHandler(LogLevel.DEBUG));
			
		});
	}

}
