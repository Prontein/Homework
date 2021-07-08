package ru.gb.server.core.networkservice;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import ru.gb.server.core.comanddictionary.dictionaryservice.CommandDictionaryService;
import ru.gb.server.core.commandhandler.CommandHandler;
import ru.gb.server.core.networkservice.serverservice.ServerService;
import ru.gb.server.util.PropertyUtils;

public class NettyServerService implements ServerService {

    private final CommandDictionaryService dictionaryService;

    public NettyServerService(CommandDictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

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
                            channel.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(Integer.parseInt(PropertyUtils.getProperties("BUFFER_SIZE"))));
                            channel.pipeline()
                                    .addLast(new StringDecoder())
                                    .addLast(new StringEncoder())
                                    .addLast(new CommandHandler(dictionaryService));
                        }
                    });
            ChannelFuture future = bootstrap.bind(Integer.parseInt(PropertyUtils.getProperties("PORT"))).sync();
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
