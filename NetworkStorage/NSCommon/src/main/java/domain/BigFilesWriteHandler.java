package domain;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class BigFilesWriteHandler extends ChannelInboundHandlerAdapter {

    private final String IN_OUT_OBJECT_HANDLER = "ChunkedWriteHandler";
    private final String BIF_FILE_HANDLER = "BigFilesWriteHandler";

    private String filename;
    private Long fileSize;

    public BigFilesWriteHandler (String copyFilename, Long fileSize) {
        this.filename = copyFilename;
        this.fileSize = fileSize;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object chunkedFile) {
        ByteBuf byteBuf = (ByteBuf) chunkedFile;
        loadFile(byteBuf);
        byteBuf.release();

        if (isWriteComplete(filename)) {

            ctx.pipeline().remove(IN_OUT_OBJECT_HANDLER);
            ctx.pipeline().remove(BIF_FILE_HANDLER);

            MessageDTO messageInfo = new MessageDTO();
            messageInfo.setMessageType(MessageType.FILE_COPY_COMPLETE);
            messageInfo.setCatalogName(filename);
            ctx.pipeline().fireChannelRead(messageInfo.convertToJson());
        }

    }


    private void loadFile (ByteBuf byteBuf) {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(filename, true))) {
            while (byteBuf.isReadable()) {
                os.write(byteBuf.readByte());
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private boolean isWriteComplete (String filename) {
        File file = new File(filename);
        return file.length() == fileSize;
    }
}
