package Server;

import ReinforceLearning.ReinforceLearningAgentConnectFour;
import dao.QTableDao;
import dto.Connect4Dto;
import dto.QTableDto;
import handler.BaseHandler;
import handler.HandlerFactory;
import handler.StatusCodes;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import request.CustomParser;
import request.ParsedRequest;
import response.CustomHttpResponse;
import response.HttpResponseBuilder;
import target.Connect4;

public class Server {

    public static void main(String[] args) {
        Calendar.getInstance();
        ServerSocket serverSocket;
        Socket socket = null;
        Connect4 game = new Connect4();
        QTableDto qTableDto = new QTableDto();
        Connect4Dto connect4Dto = new Connect4Dto(game);
        ReinforceLearningAgentConnectFour Agent = new ReinforceLearningAgentConnectFour(connect4Dto,qTableDto);

        try {
            serverSocket = new ServerSocket(1299);
            System.out.println("Opened socket " + 1299);
            while (true) {
                // keeps listening for new clients, one at a time
                if(connect4Dto.isGameOver()){
                    game.resetBoard();
                    connect4Dto.clearAction();
                }
                try {
                    socket = serverSocket.accept(); // waits for client here
                } catch (IOException e) {
                    System.out.println("Error opening socket");
                    System.exit(1);
                }

                InputStream stream = socket.getInputStream();
                byte[] b = new byte[1024*20];
                stream.read(b);
                String input = new String(b).trim();
                System.out.println(input);

                BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
                PrintWriter writer = new PrintWriter(out, true);  // char output to the client

                // HTTP Response
                if(!input.isEmpty()){
                    writer.println(processRequest(input,Agent));
                }else{
                    writer.println("HTTP/1.1 200 OK");
                    writer.println("Server: TEST");
                    writer.println("Connection: close");
                    writer.println("Content-type: text/html");
                    writer.println("");
                }

                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Error opening socket");
            System.exit(1);
        }
    }

    // Assume the http server feeds the entire raw http request here
    // Response is a raw http response string
    public static String processRequest(String requestString, ReinforceLearningAgentConnectFour Agent) {
        try{
            ParsedRequest request = CustomParser.parse(requestString);
            BaseHandler handler = HandlerFactory.getHandler(request,Agent);
            var builder = handler.handleRequest(request);
            builder.setHeader("Content-Type", "application/json");

            var httpRes = builder.build();
            return httpRes.toString();
        }catch (Exception e){
            return new HttpResponseBuilder()
                    .setStatus(StatusCodes.SERVER_ERROR)
                    .setBody(e.toString())
                    .build()
                    .toString();
        }
    }
}
