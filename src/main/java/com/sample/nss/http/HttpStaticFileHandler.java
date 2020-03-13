package com.sample.nss.http;

import java.io.IOException;
import java.io.RandomAccessFile;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;
import lombok.AllArgsConstructor;

public class HttpStaticFileHandler extends SimpleChannelInboundHandler<FullHttpRequest>{
	private String path;
	private String filename;
	
	public HttpStaticFileHandler(String path, String filename) {
        super(false); // set auto-release to false
        this.path = path;
        this.filename = filename;
    }
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		if (!this.path.equals(req.getUri())) {
			// req.retain();
			System.out.println("HttpStaticFileHandler refCnt : " + ReferenceCountUtil.refCnt(req));
			ctx.fireChannelRead(req);
		} else {
			sendStaticFile(ctx,req);
		}
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		// ctx.close();
	}
	
	private void sendStaticFile(ChannelHandlerContext ctx, FullHttpRequest req) throws IOException {
        try {
            RandomAccessFile raf = new RandomAccessFile(filename, "r");
            long fileLength = raf.length();

            HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            HttpUtil.setContentLength(res, fileLength);
            res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=utf-8");
            if (HttpUtil.isKeepAlive(req)) {
                res.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            ctx.write(res); // 응답 헤더 전송
            ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength));
            ChannelFuture f = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if (!HttpUtil.isKeepAlive(req)) {
                f.addListener(ChannelFutureListener.CLOSE);
            }
        } finally {
            req.release();
        }
    }

	
}
