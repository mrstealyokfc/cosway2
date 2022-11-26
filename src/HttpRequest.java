import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String httpVersion;
    private String location;
    private Map<String,String> headers = new HashMap<>();
    private InputStream content;

    public static HttpRequest fromStream(InputStream stream) throws IOException {
        HttpRequest request = new HttpRequest();

        String[] headline = Utils.getNextLine(stream).trim().split(" ");
        if(headline.length!=3)
            throw new IOException("Bad Http Request");
        request.method = headline[0].trim();
        request.location = headline[1].trim();
        request.httpVersion = headline[2].trim();

        while(true){
            String line = Utils.getNextLine(stream).trim();
            if(line.length()==0)
                break;
            String[] split = line.split(": ");
            if(split.length!=2)
                throw new IOException("Bad Request");
            request.headers.put(split[0].trim(),split[1].trim());
        }

        request.content=stream;

        return request;
    }



    public String getName(){
        return method+":"+location;
    }

    public HttpRequest(){

    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(method).append(" ").append(location).append(" ").append(httpVersion).append("\n");
        for(String s : headers.keySet())
            sb.append(s).append(": ").append(headers.get(s)).append("\n");
        sb.append("\n");
        return sb.toString();
    }

}
