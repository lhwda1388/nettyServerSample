package com.sample.nss.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class HttpNotFoundHandler extends SimpleChannelInboundHandler<FullHttpRequest>{
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("HttpNotFoundHandler refCnt : " + ReferenceCountUtil.refCnt(req));
		ByteBuf buf = Unpooled.copiedBuffer("Not found", CharsetUtil.UTF_8);
		FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, buf);
		res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=utf-8");
        if (HttpUtil.isKeepAlive(req)) {
            res.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        res.headers().set(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
        ctx.writeAndFlush(res).addListener((ChannelFuture f) -> {
        	if (!HttpUtil.isKeepAlive(req)) {
        		f.channel().close();
        	}
        });
	}
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
	}
}
