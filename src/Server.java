import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server extends Thread{

    private static Server theServer;

    private RequestHandler requestHandler = new RequestHandler();

    private ServerSocket serverSocket;

    public static Server Instance(){
        return theServer;
    }

    public Server(int port) {
        if(theServer!=null)
            throw new RuntimeException("Server Constructor called while Server is already online");
        try{
            this.serverSocket = new ServerSocket(port);
        }catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("Server Started on port "+port+" http://127.0.0.1:"+port);
    }

    public static void init(){
        init(8080);
    }

    public static void init(int port){
        theServer = new Server(port);
        theServer.start();
    }

    public void run(){
        while(true){
            try {
                Socket s = serverSocket.accept();
                requestHandler.handleRequest(s);
            } catch (IOException e) {
                e.printStackTrace();
                if(serverSocket.isClosed())
                    break;
            }
        }
        System.out.println("Server Closed");
    }

    public static void serveStaticDir(String dir, String serverDir){
        if(theServer==null)
            init();
        List<File> allFiles = getAllFiles(new File(dir));
        for(File f : allFiles){
            System.out.println(f.getPath());
        }
        for(File f : allFiles){
            String path = f.getPath().substring(f.getPath().indexOf(dir)+dir.length());
            theServer.requestHandler.registerEndpoint("get:"+serverDir + path, new ObjectEndpoint() {
                @Override
                public Object process(HttpRequest request) {
                    addHeader("Content-Type",Utils.getMimeType(f.getName()));
                    try{
                        return new String(new FileInputStream(f).readAllBytes());
                    }catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            });
        }
        System.out.println(dir);
    }

    public static List<File> getAllFiles(File root){
        if(!root.exists())
            return Collections.emptyList();
        if(root.isFile())
            return List.of(root);
        ArrayList<File> retv = new ArrayList<>();
        for (File f : root.listFiles())
            retv.addAll(getAllFiles(f));
        return retv;
    }

    public static void close(){
        theServer._close();
    }

    private void _close(){
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
