package reservation.Redis;

import java.io.IOException;

import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

//@Component
//public class RedisShutdow {
//
//	@EventListener(ContextClosedEvent.class)
//	public void stopRedisServer() {
//		try {
//			ProcessBuilder processBuilder = new ProcessBuilder("/opt/homebrew/bin/redis-cli", "shutdown");
//			processBuilder.inheritIO();
//			processBuilder.start();
//			System.out.println("Redis server stop");
//		}catch (IOException e){
//			throw new RuntimeException("Failed to stop Redis Server", e);
//		}
//	}
//}
