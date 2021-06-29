package ru.gb.server.core.networkservice;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import ru.gb.server.core.commandhandler.CommandHandler;
import ru.gb.server.core.networkservice.networkInterface.ServerService;

public class NettyServerService implements ServerService {

    private static final int SERVER_PORT = 8189;

    @Override
    public void startServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(9048));
                            channel.pipeline()
                                    .addLast(new StringDecoder())
                                    .addLast(new StringEncoder())
                                    .addLast(new CommandHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(SERVER_PORT).sync();
            System.out.println("Server start");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            System.out.println("Server shutdown");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
