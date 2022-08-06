package com.l1nker4.serverdemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class SimpleServer {

    public static void main(String[] args) {
        /**
         * ServerBootstrap：服务端启动器，负责组装、协调netty组件
         * NioEventLoopGroup：thread + selector
         * NioServerSocketChannel：对原生NIO的ServerSocketChannel封装
         * ChannelInitializer：对channel进行初始化
         */
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    //连接建立后执行initChannel
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //StringDecoder：将Bytebuffer转为string
                        ch.pipeline().addLast(new StringDecoder());
                        //自定义handler
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(msg);
                            }
                        });
                    }
                })
                .bind(8080);
    }
}
