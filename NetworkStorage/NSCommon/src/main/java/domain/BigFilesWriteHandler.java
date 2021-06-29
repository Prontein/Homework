package domain;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class BigFilesWriteHandler extends ChannelInboundHandlerAdapter {

    private String filename;
    private Long fileSize;

    public BigFilesWriteHandler (String copyFilename, Long fileSize) {
        this.filename = copyFilename;
        this.fileSize = fileSize;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object chunkedFile) {
        ByteBuf byteBuf = (ByteBuf) chunkedFile;
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(filename, true))) {
            while (byteBuf.isReadable()) {
                os.write(byteBuf.readByte());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (isWriteComplete(filename)) {
            ctx.pipeline().remove("1");
            ctx.pipeline().remove("2");
        }
        byteBuf.release();
    }

    private boolean isWriteComplete (String filename) {
        File file = new File(filename);
        return file.length() == fileSize;
    }
}
