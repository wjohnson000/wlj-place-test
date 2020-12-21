package std.wlj.compression;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.util.POJOMarshalUtil;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {

    static Jackson2JsonRedisSerializer<RootModel> jackson2Json;
    static {
        ObjectMapper om = new ObjectMapper();
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2Json = new Jackson2JsonRedisSerializer<>(RootModel.class);
    }

    public static String readInterpJSON() throws Exception {
        URL url = std.wlj.compression.Util.class.getClassLoader().getResource("interp.json");
        byte[] bytes = Files.readAllBytes(Paths.get(url.toURI()));
        return new String(bytes);
    }

    public static String readInterpXML() throws Exception {
        URL url = std.wlj.compression.Util.class.getClassLoader().getResource("interp.xml");
        byte[] bytes = Files.readAllBytes(Paths.get(url.toURI()));
        return new String(bytes);
    }

    public static String jackson2Json() throws Exception {
        RootModel root = readInterpJackson2();
        return new String(jackson2Json.serialize(root));
    }

    public static RootModel readInterpPOJOMarshaler() throws Exception {
        String json = readInterpJSON();
        return POJOMarshalUtil.fromJSON(json, RootModel.class);
    }

    public static RootModel readInterpJackson2() throws Exception {
        String json = readInterpJSON();
        return jackson2Json.deserialize(json.getBytes());
    }
}
