package cn.isheihei.nio.aio;

import cn.isheihei.nio.bytebuffer.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
public class AioFileChannel {
    public static void main(String[] args) {
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get("netty-demo/data.txt"), StandardOpenOption.READ)) {

            ByteBuffer buffer = ByteBuffer.allocate(5);
            /**
             * 参数1 byteBuffer
             * 参数2 读取的起始位置
             * 参数3 附件
             * 参数4 回调对象
             */
            log.debug("read begin...");
            channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override// 成功
                public void completed(Integer result, ByteBuffer attachment) {
                    log.debug("read completed...{}", result);
                    attachment.flip();
                    ByteBufferUtil.debugAll(attachment);
                }

                @Override// 失败
                public void failed(Throwable exc, ByteBuffer attachment) {
                    exc.printStackTrace();
                }
            });
            log.debug("read end");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
