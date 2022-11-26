import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestHandler {

    private ExecutorService threadPool = Executors.newCachedThreadPool();

    private ConcurrentHashMap<String,Endpoint> mappings = new ConcurrentHashMap<>();

    public void handleRequest(Socket client){
        threadPool.submit(new Request(client,mappings));
    }

    public void registerEndpoint(String name,Endpoint endpoint){
        mappings.put(name.toLowerCase(),endpoint);
    }

}

class Request implements Runnable{

    private Socket client;
    private ConcurrentHashMap<String,Endpoint> mappings;
    private Endpoint error404 = new ObjectEndpoint() {
        @Override
        public Object process(HttpRequest request) {
            return "404 not found";
        }
    };
    public Request(Socket client,ConcurrentHashMap<String,Endpoint> mappings){
        this.client=client;
        this.mappings=mappings;
    }

    @Override
    public void run() {
        try {
            while(!client.isClosed()){
                HttpRequest request = HttpRequest.fromStream(client.getInputStream());
                Endpoint handle = mappings.get(request.getName().toLowerCase());
                if(handle==null)
                    error404.get(request)
                            .setMessage("404 NOTFOUND")
                            .setCode(400)
                            .sendTo(client.getOutputStream());
                else
                    handle.get(request).sendTo(client.getOutputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            this.client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
