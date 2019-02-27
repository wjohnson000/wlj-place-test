/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.familysearch.standards.place.data.PlaceRepBridge;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Helper class to manage Jackson->JSON serializers of two types:  {@link CacheModel} and byte[].
 * The latter is used as when compressing the JSON to minimize the size of the data being stored.
 * 
 * @author wjohnson000
 *
 */
public abstract class RedisSerializer {

    private static final Jackson2JsonRedisSerializer<PlaceRepBridge> placeRepBridgeSerializer = makePlaceRepBridgeSerializer();
    private static final Jackson2JsonRedisSerializer<byte[]>         byteArraySerializer = makeByteArraySerializer();

    public static Jackson2JsonRedisSerializer<byte[]> getByteArraySerializer() {
        return byteArraySerializer;
    }

    /**
     * Convert a {@link PlaceRepBridge} to a byte array for storing in Redis.  Because of the
     * potentially large size of the model, zip the contents and byte-ize the compressed data.
     * 
     * @param cacheModel cache model to process
     * @return bytes comprising the compressed model data
     * @throws Exception
     */
    public static byte[] placeRepBridgeToBytes(PlaceRepBridge repBridge) throws Exception {
        byte[] json = placeRepBridgeSerializer.serialize(repBridge);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(json.length);
        GZIPOutputStream      gzos = new GZIPOutputStream(baos);
        gzos.write(json);
        gzos.flush();
        gzos.close();

        return baos.toByteArray();
    }

    /**
     * Convert a compressed array of bytes to a {@link PlaceRepBridge} instance.
     * 
     * @param json raw byte array
     * @return PlaceRepBridge instance
     * @throws Exception
     */
    public static PlaceRepBridge bytesToPlaceRepBridge(byte[] json) throws Exception {
        int nread;
        byte[] buffer = new byte[2048];
        ByteArrayInputStream bais = new ByteArrayInputStream(json);
        GZIPInputStream      gzip = new GZIPInputStream(bais);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((nread = gzip.read(buffer, 0, buffer.length)) != -1) {
            baos.write(buffer, 0, nread);
        }
        gzip.close();

        return placeRepBridgeSerializer.deserialize(baos.toByteArray());
    }

    static Jackson2JsonRedisSerializer<PlaceRepBridge> makePlaceRepBridgeSerializer() {
        ObjectMapper om = new ObjectMapper();
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        Jackson2JsonRedisSerializer<PlaceRepBridge> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(PlaceRepBridge.class);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        return jackson2JsonRedisSerializer;
    }

    static Jackson2JsonRedisSerializer<byte[]> makeByteArraySerializer() {
        ObjectMapper om = new ObjectMapper();
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        Jackson2JsonRedisSerializer<byte[]> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(byte[].class);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        return jackson2JsonRedisSerializer;
    }
}