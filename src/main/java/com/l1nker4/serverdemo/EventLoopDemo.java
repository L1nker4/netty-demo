package com.l1nker4.serverdemo;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class EventLoopDemo {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        Set<EventLoop> set = new HashSet<>();
        while(group.next() != null){
            EventLoop next = group.next();
            if (!set.contains(next)){
                set.add(next);
            }else {
                break;
            }
        }
        System.out.println(set);
        group.next().scheduleAtFixedRate(() -> {
            log.debug("hello");
        }, 3,2, TimeUnit.SECONDS);
        log.debug("ok");
    }
}
