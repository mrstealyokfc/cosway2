import java.util.HashMap;
import java.util.Map;

public abstract class ObjectEndpoint implements Endpoint{

    private Map<String,String> headers = new HashMap<>();
    @Override
    public HttpResponse get(HttpRequest request) {
        headers.clear();
        Object response = process(request);
        if(response==null)
            return new HttpResponse(500)
                    .setMessage("INTERNAL SERVER ERROR")
                    .addHeader("server","yourmom.com");
        HttpResponse resp = HttpResponse.fromString(response.toString());
        for(String s : headers.keySet())
            resp.addHeader(s,headers.get(s));
        resp.setCode(200);
        resp.setMessage("OK");
        return resp;
    }

    public void addHeader(String key,String value){
        headers.put(key,value);
    }

    /*
     *  converts the object returned to a string and then sends it as the content of a http 200 response
     *  if the object returned is null sends an http 500 error
     */
    public abstract Object process(HttpRequest request);
}
