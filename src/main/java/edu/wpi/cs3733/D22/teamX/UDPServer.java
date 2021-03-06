package edu.wpi.cs3733.D22.teamX;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UDPServer {
  private DatagramChannel server;

  private UDPServer() {
    try {
      server = startServer();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static class SingletonHelper {
    private static final UDPServer udpServer = new UDPServer();
  }

  public static UDPServer getUDPServer() {
    return SingletonHelper.udpServer;
  }

  public String receive() {
    try {
      return receiveMessage(server);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void send(String msg, SocketAddress serverAddress) {
    try {
      sendMessage(server, msg, serverAddress);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void getValue(String msg, SocketAddress serverAddress) {
    try {
      sendMessage(server, ("GET" + msg), serverAddress);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void setTemp(double Temp, SocketAddress serverAddress) {
    try {
      sendMessage(server, ("TEM" + Temp), serverAddress);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void setFan(boolean isOn, SocketAddress serverAddress) {
    try {
      if (isOn) {
        sendMessage(server, ("FAN" + "on"), serverAddress);
      } else {
        sendMessage(server, ("FAN" + "off"), serverAddress);
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void setLamp(boolean isOn, SocketAddress serverAddress) {
    try {
      if (isOn) {
        sendMessage(server, ("LIT" + "on"), serverAddress);
      } else {
        sendMessage(server, ("LIT" + "off"), serverAddress);
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static DatagramChannel startServer() throws IOException {
    InetSocketAddress address = new InetSocketAddress("0.0.0.0", 6587);
    DatagramChannel server = bindChannel(address);
    System.out.println("Server started at #" + address);
    server.configureBlocking(false);
    return server;
  }

  private static String extractMessage(ByteBuffer buffer) {
    buffer.flip();
    byte[] bytes = new byte[buffer.remaining()];
    buffer.get(bytes);
    String msg = new String(bytes);

    return msg;
  }

  private static String receiveMessage(DatagramChannel server) throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    SocketAddress remoteAdd = server.receive(buffer);
    if (remoteAdd != null) {
      String message = extractMessage(buffer);
      return message;
    }
    return "";
  }

  public static DatagramChannel openChannel() throws IOException {
    DatagramChannel datagramChannel = DatagramChannel.open();
    return datagramChannel;
  }

  public static DatagramChannel bindChannel(SocketAddress local) throws IOException {
    return openChannel().bind(local);
  }

  public static void sendMessage(DatagramChannel client, String msg, SocketAddress serverAddress)
      throws IOException {
    ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
    client.send(buffer, serverAddress);
  }
}
