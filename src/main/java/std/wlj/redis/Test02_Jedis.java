package std.wlj.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import std.wlj.marshal.POJOMarshalUtil;

/**
 * public static Collection<Object[]> testParams(){
 *  JedisConnectionFactory jedisConnFactory=new JedisConnectionFactory();
 *  jedisConnFactory.setPort(SettingsUtils.getPort());
 *  jedisConnFactory.setHostName(SettingsUtils.getHost());
 *  jedisConnFactory.afterPropertiesSet();
 *  return Arrays.asList(new Object[][]{{jedisConnFactory}});
}

 * @author wjohnson000
 *
 */
public class Test02_Jedis {

    // Sentinal configuration ...
    static RedisSentinelConfiguration sentinelConfig =
            new RedisSentinelConfiguration() .master("mymaster")
            .sentinel("ps-std-ws-stdplace55.h9yqki.0001.use1.cache.amazonaws.com", 6379);

    // Connection factory ...
    static JedisConnectionFactory jedisFactory = new JedisConnectionFactory(sentinelConfig);

    static RedisTemplate<String, String> stringTemplate = new RedisTemplate<String, String>();
    static RedisTemplate<String, RootModel> rootTemplate = new RedisTemplate<String, RootModel>();

    // Static set-up ...
    static {
        stringTemplate.setConnectionFactory(jedisFactory);
        stringTemplate.setKeySerializer(new StringRedisSerializer());
        stringTemplate.setValueSerializer(new StringRedisSerializer());
        stringTemplate.afterPropertiesSet();

        Jackson2JsonRedisSerializer<RootModel> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(RootModel.class);
        ObjectMapper om = new ObjectMapper();
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        rootTemplate.setConnectionFactory(jedisFactory);
        rootTemplate.setKeySerializer(new StringRedisSerializer());
        rootTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        rootTemplate.afterPropertiesSet();
    }

    public static void main(String...args) {
//        RootModel modelIn = getRootModel();
//        String    jsonIn  = toJSON(modelIn);

//        ValueOperations<String, String> valOps = stringTemplate.opsForValue();
//        valOps.set("abc", jsonIn, 60, TimeUnit.MINUTES);
//
//        String    jsonOut  = valOps.get("abc");
//        RootModel modelOut = fromJSON(jsonOut);

        ValueOperations<String, RootModel> valOps = rootTemplate.opsForValue();
        valOps.set("abc", getRootModel(), 60, TimeUnit.MINUTES);
        RootModel modelOut = valOps.get("abc");
        System.out.println(modelOut);
    }

    static RootModel getRootModel() {
        PlaceRepresentationModel prModel = new PlaceRepresentationModel();
        prModel.setCreateDate(new java.util.Date());
        prModel.setId(111);
        prModel.setFromYear(1900);
        prModel.setToYear(2020);
        prModel.setTypeCategory("the-type-category");

        RootModel model = new RootModel();
        model.setPlaceRepresentation(prModel);

        return model;
    }

    static String toJSON(RootModel model) {
        return POJOMarshalUtil.toJSONPlain(model);
    }

    static RootModel fromJSON(String json) {
        return POJOMarshalUtil.fromJSON(json, RootModel.class);
    }
}
