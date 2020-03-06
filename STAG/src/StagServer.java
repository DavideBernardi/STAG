import java.io.*;
import java.net.*;

class StagServer
{
    GameController controller;

    public static void main(String[] args) {
        if(args.length != 2) System.out.println("Usage: java StagServer <entity-file> <action-file>");
        else new StagServer(args[0], args[1], 8888);
    }

    public StagServer(String entityFilename, String actionFilename, int portNumber)  {
        StagParser parser = new StagParser(entityFilename, actionFilename);
        /*Can I do the parsing INSIDE the constructor of the controller? Makes more sense in my head since
        * then I only call controller from the "server view", and all the complexity is handled internally.
        * But we were told to have no logic inside a constructor and parsing 2 whole files sounds like logic.
        * Could also have a function inside controller called parseGame(String entityFile, String actionFile)
        * which I would then call from here? Having it in the constructor still feels cleaner though  */
        controller = new GameController(parser.parseGame());
        try {
            controller.setUnplaced();
        } catch (EntityTypeException e) {
            System.out.println(e.toString());
        }
        controller.describeGameState();

        try {
            ServerSocket ss = new ServerSocket(portNumber);
            System.out.println("Server Listening");
            while(true) acceptNextConnection(ss);
        } catch(IOException ioe) {
            System.err.println(ioe.toString());
        }
    }

    private void acceptNextConnection(ServerSocket ss) {
        try {
            // Next line will block until a connection is received
            Socket socket = ss.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            controller.processNextCommand(in, out);
            out.close();
            in.close();
            socket.close();
        } catch(IOException ioe) {
            System.err.println(ioe.toString());
        }
    }
}


