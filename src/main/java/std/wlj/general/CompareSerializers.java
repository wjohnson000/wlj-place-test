package std.wlj.general;

import org.familysearch.standards.place.ws.model.RootModel;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import std.wlj.marshal.POJOMarshalUtil;

public class CompareSerializers {

    public static void main(String...args) {
        ObjectMapper om = new ObjectMapper();
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        Jackson2JsonRedisSerializer<RootModel> jjSerializer = new Jackson2JsonRedisSerializer<>(RootModel.class);
        jjSerializer.setObjectMapper(om);

        RootModel rm = BaseTest.getRootModel();
        String jsonA = POJOMarshalUtil.toJSON(rm);
        RootModel toModel = POJOMarshalUtil.fromJSON(jsonA, RootModel.class);

        byte[] bytes = jjSerializer.serialize(rm);
        toModel = jjSerializer.deserialize(bytes);

        long timePOa = 0L, timePOb = 0;
        long timeJJa = 0L, timeJJb = 0;
        long time0, time1, time2;

        for (int i=0;  i<500;  i++) {
            rm = BaseTest.getRootModel();

            time0 = System.nanoTime();
            jsonA = POJOMarshalUtil.toJSON(rm);
            time1 = System.nanoTime();
            toModel = POJOMarshalUtil.fromJSON(jsonA, RootModel.class);
            time2 = System.nanoTime();
            timePOa += time1 - time0;
            timePOb += time2 - time1;

            time0 = System.nanoTime();
            bytes = jjSerializer.serialize(rm);
            time1 = System.nanoTime();
            toModel = jjSerializer.deserialize(bytes);
            time2 = System.nanoTime();
            timeJJa += time1 - time0;
            timeJJb += time2 - time1;
        }

        System.out.println("POJO: " + (timePOa/1_000_000.0) + " .. " + (timePOb/1_000_000.0));
        System.out.println("JJSE: " + (timeJJa/1_000_000.0) + " .. " + (timeJJb/1_000_000.0));
    }
}
