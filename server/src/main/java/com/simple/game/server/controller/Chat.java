package com.simple.game.server.controller;


import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

@Component
@ServerEndpoint(value="/ws/chat/{nickName}")
public class Chat {
    private static final Set<Chat> connections = new CopyOnWriteArraySet<Chat>();
    private String nickName; //接收用户名称
    private Session session; //建立的会话


    public Chat(){

    }

    /*
     * 打开连接
     */
    @OnOpen
    public void onOpen(Session session,@PathParam(value="nickName") String nickName){
        this.session=session;
        this.nickName=nickName;
        connections.add(this);
        System.out.println("新用户连接进入,名字是："+this.nickName);
        String message=String.format("System>%s %s",this.nickName,"hasjoined.");
        Chat.broadCast(message);

    }
    /*
     * 关闭连接
     */
    @OnClose
    public void onClose(){
        connections.remove(this);
        String message=String.format("System> %s, %s", this.nickName,
                " has disconnection.");
        Chat.broadCast(message);
    }

    /*
     * 接收信息
     */
    @OnMessage
    public void onMessage(String message,@PathParam(value="nickName")String nickName){
        System.out.println("新消息from："+nickName+" : "+message);
        Chat.broadCast(nickName+">"+message);
    }
    /*
     * 错误消息
     */
    @OnError
    public void onError(Throwable throwable){
        System.out.println(throwable.getMessage());
    }
    /*
     * 广播消息
     */
    private static void broadCast(String message){
        for(Chat chat:connections){
            try{
                synchronized (chat) { //线程同步控制并发访问
                    chat.session.getBasicRemote().sendText(message);
                }
            }catch(IOException e){
                connections.remove(chat);
                try{
                    chat.session.close();

                }catch(IOException e1){
                    chat.broadCast(String.format("System> %s %s", chat.nickName,
                            " has bean disconnection."));
                }
            }
        }
    }
}