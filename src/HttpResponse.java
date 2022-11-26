import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final String httpVersion = "HTTP/1.1";

    private int code;
    private String message="NULL";

    private InputStream content;
    private Map<String,String> headers = new HashMap<>();

    public HttpResponse(int code){
        this.code=code;
    }

    public HttpResponse setMessage(String message){
        this.message=message;
        return this;
    }

    public HttpResponse addHeader(String key,String value){
        headers.put(key,value);
        return this;
    }

    public static HttpResponse fromString(String str){
        if(str==null)
            return new HttpResponse(500).setMessage("INTERNAL SERVER ERROR");
        HttpResponse retv = new HttpResponse(200);
        retv.setMessage("OK");
        retv.addHeader("Server","yourmom.com");
        retv.addHeader("Content-Length", String.valueOf(str.length()));
        try{
            retv.setContent(new ByteArrayInputStream(str.getBytes()));
        }catch(IOException e){
            return new HttpResponse(500).setMessage("INTERNAL SERVER ERROR");
        }
        return retv;
    }

    public void setContent(InputStream content) throws IOException {
        this.addHeader("Content-Length",String.valueOf(content.available()));
        this.content=content;
    }

    public void sendTo(OutputStream output) throws IOException {
        output.write((httpVersion+" "+code+" "+message+"\r\n").getBytes());
        for(String s : headers.keySet())
            output.write((s+": "+headers.get(s)+"\r\n").getBytes());
        output.write("\r\n".getBytes());
        if(this.content!=null)
            output.write(content.readAllBytes());
    }

    public HttpResponse setCode(int code){
        this.code=code;
        return this;
    }
}
