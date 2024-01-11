package cn.ares.boot.util.common.network;


import static cn.ares.boot.util.common.constant.StringConstant.HTTPS;
import static cn.ares.boot.util.common.constant.StringConstant.HTTPS_DEFAULT_PORT;
import static cn.ares.boot.util.common.constant.StringConstant.HTTP_DEFAULT_PORT;
import static cn.ares.boot.util.common.constant.SymbolConstant.MINUS;

import cn.ares.boot.util.common.StringUtil;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author: Ares
 * @time: 2020-05-14 15:59:00
 * @description: 网络操作工具类
 * @description: Network util
 * @version: JDK 1.8
 */
public class NetworkUtil {

  /**
   * 默认的localhost
   */
  public static final String DEFAULT_LOCALHOST = "127.0.0.1";

  /**
   * @author: Ares
   * @description: 获取mac地址
   * @description: Get mac address
   * @time: 2020-05-14 16:00:00
   * @params: [macConnector] mac地址连接符
   * @return: java.lang.String mac地址
   */
  public static String getMac(String macConnector) throws SocketException, UnknownHostException {
    StringBuilder macAddress = new StringBuilder();
    List<String> ipList = getLocalHostAddress();
    for (String str : ipList) {
      InetAddress address = InetAddress.getByName(str);
      // 获得网络接口对象(即网卡), 并得到mac地址, mac地址存在于一个byte数组中。
      // Get the network interface object (ie network card), and get the mac address, which exists in a byte array.
      byte[] mac = NetworkInterface.getByInetAddress(address).getHardwareAddress();
      if (null != mac) {
        StringBuilder segment = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
          if (i != 0) {
            segment.append(macConnector);
          }
          // mac[i] & 0xFF 是为了把byte转化为正整数
          // mac[i] & 0xFF is to convert byte to positive integer
          String hexMac = Integer.toHexString(mac[i] & 0xFF);
          segment.append(hexMac.length() == 1 ? 0 + hexMac : hexMac);
        }
        // 把字符串所有小写字母改为大写成为正规的mac地址并返回
        // change all lowercase letters of the string to uppercase to become a regular mac address and return
        macAddress.append(segment.toString().toUpperCase());
      }
    }
    return macAddress.toString();
  }

  /**
   * @author: Ares
   * @description: 以默认连接符获取mac地址
   * @description: Get mac address with default connector
   * @time: 2020-05-14 16:00
   * @params: [] 请求参数
   * @return: java.lang.String mac地址
   */
  public static String getMac() throws SocketException, UnknownHostException {
    return getMac(MINUS);
  }

  /**
   * @author: Ares
   * @description: 获取本机ip列表
   * @description: Get the local ip list
   * @time: 2020-05-14 16:02:00
   * @params: [] 请求参数
   * @return: java.util.List<java.lang.String> ip列表
   */
  public static List<String> getLocalHostAddress() throws SocketException {
    List<String> ipList = new ArrayList<>();
    Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
    while (networkInterfaces.hasMoreElements()) {
      NetworkInterface networkInterface = networkInterfaces.nextElement();
      Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
      while (addresses.hasMoreElements()) {
        InetAddress address = addresses.nextElement();
        if (address instanceof Inet4Address) {
          if (!DEFAULT_LOCALHOST.equals(address.getHostAddress())) {
            ipList.add(address.getHostAddress());
          }
        }
      }
    }
    return ipList;
  }

  /**
   * @author: Ares
   * @description: 获取最后一个ip地址
   * @description: Get the last ip address
   * @time: 2022-06-08 14:07:07
   * @params: [] in 入参
   * @return: java.lang.String out 出参
   */
  public static String getLastLocalHostAddress() {
    List<String> ipList;
    try {
      ipList = getLocalHostAddress();
      // 取最后一个
      // Get the last
      return ipList.get(ipList.size() - 1);
    } catch (SocketException socketException) {
      socketException.printStackTrace();
    }
    return DEFAULT_LOCALHOST;
  }

  /**
   * @author: Ares
   * @description: ip字符串转整形
   * @description: Ip str to int
   * @time: 2021-07-29 16:01:00
   * @params: [ipStr] ip字符串
   * @return: int int ip
   */
  public static int ipToInt(String ipStr) {
    String[] ip = ipStr.split("\\.");
    return (Integer.parseInt(ip[0]) << 24) + (Integer.parseInt(ip[1]) << 16) + (
        Integer.parseInt(ip[2]) << 8) + Integer.parseInt(ip[3]);
  }

  /**
   * @author: Ares
   * @description: 整形ip转字符串
   * @description: Ip int to str
   * @time: 2021-07-29 16:02:00
   * @params: [intIp] 整形ip
   * @return: java.lang.String ip字符串
   */
  public static String intToIp(int intIp) {
    return (intIp >> 24) + "."
        + ((intIp & 0x00FFFFFF) >> 16) + "."
        + ((intIp & 0x0000FFFF) >> 8) + "."
        + (intIp & 0x000000FF);
  }

  /**
   * 从给定的URI中提取主机
   * Extract the host from the given URI.
   *
   * @param uri the URI to extract the host from
   * @return the extracted host
   */
  public static String extractHost(URI uri) {
    String host = uri.getHost();
    if (StringUtil.isBlank(host)) {
      String urlStr = uri.toString();
      host = StringUtil.listSplit(urlStr, "/").get(0);
    }
    return host;
  }

  /**
   * 从给定的URI中提取端口
   * Extract the port from the given URI.
   *
   * @param uri the URI to extract the port from
   * @return the extracted port
   */
  public static int extractPort(URI uri) {
    int port = uri.getPort();
    if (port < 0) {
      if (HTTPS.equals(uri.getScheme())) {
        port = HTTPS_DEFAULT_PORT;
      } else {
        port = HTTP_DEFAULT_PORT;
      }
    }
    return port;
  }

}
