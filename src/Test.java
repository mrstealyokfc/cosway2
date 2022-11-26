/*
 *
 * @Author someone who cant stop writing half-finished http servers
 *
 */

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        Server.init();
        Server.serveStaticDir("static","/chicken");
    }
}