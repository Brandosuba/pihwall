
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
/*
Brandon Calvario
Firewall.Java
tool to block and list IP
 */
public final class Firewall {
    public static String run(String... cmd) throws IOException, InterruptedException {
        Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String output = br.lines().collect(Collectors.joining("\n"));
            if(p.waitFor() != 0) throw new RuntimeException(output);
            return output;
        }
    }
    public static void blockIp(String ip) throws Exception {
        run("sudo","nft","add","rule","inet","filter","input","ip","saddr",ip,"drop","comment","\"pihole-auto\"");
    }
    public static String list()throws Exception{
        return run("sudo","nft","list","ruleset");
    }
}
