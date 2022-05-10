package com.simple.game.core.util;

import java.io.IOException;
import java.util.Map;

import com.simple.game.core.domain.cmd.Cmd;

/***
 * 参考 webSocketSession
 * 
 * @author zhibozhang
 *
 */
public interface GameSession {

    boolean isOpen();

    /**
     * Get the idle timeout for this session.
     * @return The current idle timeout for this session in milliseconds. Zero
     *         or negative values indicate an infinite timeout.
     */
    long getMaxIdleTimeout();


    /**
     * Provides a unique identifier for the session. This identifier should not
     * be relied upon to be generated from a secure random source.
     * @return A unique identifier for the session.
     */
    String getId();
    
    /***
     * 獲取遠程地址
     * @return
     */
    String getRemoteAddr();

    void close() throws IOException;
    
    /****發送數據**/
    void write(byte[] data)throws IOException;
    
    void write(String data);
    
    void write(Cmd data);
    
    Map<String, Object> getAttachment();
}
