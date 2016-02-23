/*
 * Copyright 2016 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class TelicFrameDecoder extends FrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        if (buf.readableBytes() < 4) {
            return null;
        }

        long length = buf.getUnsignedInt(buf.readerIndex());

        if (length < 1024) {
            if (buf.readableBytes() >= length + 4) {
                buf.readUnsignedInt();
                return buf.readBytes((int) length);
            }
        } else {
            int endIndex = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) 0);
            if (endIndex >= 0) {
                ChannelBuffer frame = buf.readBytes(endIndex - buf.readerIndex());
                buf.readByte();
                if (frame.readableBytes() > 0) {
                    return frame;
                }
            }
        }

        return null;
    }

}