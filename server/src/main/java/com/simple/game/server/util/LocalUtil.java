package com.simple.game.server.util;

import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalUtil {
	private static String ip;
	private static Integer pid;
	
	private static List<String> getIpAddress() throws SocketException {
	    List<String> list = new LinkedList<>();
	    Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
	    while (enumeration.hasMoreElements()) {
	        NetworkInterface network = (NetworkInterface) enumeration.nextElement();
	        if (network.isVirtual() || !network.isUp()) {
	            continue;
	        } else {
	            Enumeration addresses = network.getInetAddresses();
	            while (addresses.hasMoreElements()) {
	                InetAddress address = (InetAddress) addresses.nextElement();
	                if (address != null && (address instanceof Inet4Address || address instanceof Inet6Address)) {
	                    list.add(address.getHostAddress());
	                }
	            }
	        }
	    }
	    return list;
	}
	
	public static String getLocalIp() {
		if(ip != null) {
			return ip;
		}
		
		try {
			List<String> list = getIpAddress();
			log.info("本机ip：", list);
			
			for(String item : list) {
				if("127.0.0.1".equals(item)) {
					continue;
				}
				else if("0:0:0:0:0:0:0:1".equals(item)) {
					continue;
				}
				else if("localhost".equals(item)) {
					continue;
				}
				
				String[] array = item.split("\\.");
				if(array.length == 4) {
					ip = item;
					return ip;
				}
			}
			
		} catch (SocketException e) {
			log.warn("获取本机ip失败", e);
			return "";
		}
		
		log.warn("获取本机ip失败");
		return "";
	}
	
	public static int getLocalPid(){
		if(pid != null) {
			return pid;
		}
		String name = ManagementFactory.getRuntimeMXBean().getName();
		String[] names = name.split("@");
		pid = Integer.parseInt(names[0]);
		log.info("本机pid：{}", pid);
		return pid;
	}
}
