package charlie.mirror.client;

public class URLParser {
    public String[] parse(String pattern, String url){
        if(pattern.indexOf('[') == -1) return new String[0];
        String fSplit = url.substring(pattern.indexOf('['));
        return null;
    }
}
