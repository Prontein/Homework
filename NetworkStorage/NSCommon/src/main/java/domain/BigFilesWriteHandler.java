package domain;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class BigFilesWriteHandler extends ChannelInboundHandlerAdapter {

    private final String filename;
    private final Long fileSize;

    public BigFilesWriteHandler(String copyFilename, Long fileSize) {
        this.filename = copyFilename;
        this.fileSize = fileSize;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object chunkedFile) {
        loadFile(chunkedFile);

        if (isWriteComplete(filename)) {

            removeHandlers(ctx);

            MessageDTO messageInfo = new MessageDTO();
            messageInfo.setMessageType(MessageType.FILE_COPY_COMPLETE);
            messageInfo.setCatalogName(filename);

            ctx.pipeline().fireChannelRead(messageInfo.convertToJson());
        }
    }

    private void loadFile(Object chunkedFile) {
        ByteBuf byteBuf = (ByteBuf) chunkedFile;

        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(filename, true))) {
            while (byteBuf.isReadable()) {
                os.write(byteBuf.readByte());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            byteBuf.release();
        }
    }

    private boolean isWriteComplete(String filename) {
        File file = new File(filename);
        return file.length() == fileSize;
    }

    private void removeHandlers(ChannelHandlerContext ctx) {
        ctx.pipeline().remove(ChunkedWriteHandler.class);
        ctx.pipeline().remove(BigFilesWriteHandler.class);
    }
}
