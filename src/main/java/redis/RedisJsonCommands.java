package redis;

import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.util.SafeEncoder;

public class RedisJsonCommands implements ProtocolCommand {
    public static final RedisJsonCommands JSON_SET = new RedisJsonCommands("JSON.SET");
    public static final RedisJsonCommands JSON_GET = new RedisJsonCommands("JSON.GET");  // <-- added

    private final byte[] raw;

    private RedisJsonCommands(String command) {
        raw = SafeEncoder.encode(command);
    }

    @Override
    public byte[] getRaw() {
        return raw;
    }
}
