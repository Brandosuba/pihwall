/*
Brandon Calvario
Main.java
main class, creates a client
 */
public final class Main {
    public static void main(String[] args) throws Exception {
        PiHoleClient piHoleClient = new PiHoleClient("http://127.0.0.1:8080",System.getenv("PH_TOKEN"));
        switch(args[0]){
            case "blacklist" -> piHoleClient.addToBlockList(args[1]);
            case "toggle" -> piHoleClient.toggleBlock(!"off".equals(args[1]),60);
            case"ban" -> Firewall.blockIp(args[1]);
            case"status" -> System.out.println(piHoleClient.stats().toPrettyString());
            default -> System.out.println("Use: blacklist|toggle|ban|stats...");
        }
    }
}
