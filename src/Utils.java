import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Utils {

    public static HashMap<String,String> mimeTypes = new HashMap<>();

    static{
        try {
            String mappings = new String(
                    new Utils().getClass().getResource("mimeMappings")
                            .openStream().readAllBytes());
            String[] lines = mappings.split("\n");
            for(String s : lines){
                String[] split = s.split(" ");
                mimeTypes.put(split[0],split[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        for(String s : mimeTypes.keySet()){
            System.out.println(s+": "+mimeTypes.get(s));
        }
    }

    public static String getNextLine(InputStream input) throws IOException {
        StringBuilder  sb = new StringBuilder();
        int i;
        while((i=input.read())!=-1){
            if(i=='\r')
                continue;
            if(i=='\n')
                break;
            sb.append((char)i);
        }
        return sb.toString();
    }

    public static String getMimeType(String fname){
        String[] split = fname.split("\\.");
        if(split.length==0)
            return "text/plain";
        String retv = mimeTypes.get(split[split.length-1]);
        if(retv==null)
            return "text/plain";
        return retv;
    }

}
