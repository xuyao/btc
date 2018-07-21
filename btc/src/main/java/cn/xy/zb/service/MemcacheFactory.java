/**
 * 
 */
package cn.xy.zb.service;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;

import com.memcached.client.SockIOPool;
import com.plato.common.cache.memcached.MemcachedCache;
import com.plato.common.cache.memcached.MemcachedCacheImpl;

/**
 * memcache client factory
 */
public class MemcacheFactory {
	
	private static SockIOPool memcachedPool=null;
	static Log log = LogFactory.getLog(MemcacheFactory.class);
	static Properties properties = null;
	static MemcachedCache memcachedClient = null;
	static String size="";
	static String type="";
	static {
		try {
			ClassPathResource res = new ClassPathResource("mmc.properties");
			properties = new Properties();
			properties.load(res.getInputStream());
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}
	
	static String getPro(String name){
		return properties.get(name).toString();
	}

	static {
		try {
			size = getPro("size");
			type = getPro("type");
			memcachedPool=SockIOPool.getInstance(getPro("mccaptcha.poolname"));
			memcachedPool.setServers(getPro("mccaptcha.servers").split(","));			
			memcachedPool.setInitConn(Integer.parseInt(getPro("mccaptcha.initconn")));
			memcachedPool.setMinConn(Integer.parseInt(getPro("mccaptcha.minconn")));
			memcachedPool.setMaxConn(Integer.parseInt(getPro("mccaptcha.maxconn")));
			memcachedPool.setAliveCheck(true);
			memcachedPool.setMaintSleep(Integer.parseInt(getPro("mccaptcha.maintsleep")));
			memcachedPool.setNagle(false);
			memcachedPool.setHashingAlg(Integer.parseInt(getPro("mccaptcha.hashingAlg")));
			memcachedPool.initialize();
			String memcached_name = getPro("mccaptcha.cloud.appName");
			memcachedClient=new MemcachedCacheImpl(getPro("mccaptcha.poolname"),true,4096);
		} catch (Exception e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}
	}//
	
	public static MemcachedCache getClient(){
		return memcachedClient;
	}
	
	public static void main(String[] a){
		System.out.print(MemcacheFactory.getClient().get("hl"));
		System.out.print(",");
		System.out.print(MemcacheFactory.getClient().get("ma"));
		System.out.print(",");
		System.out.print(MemcacheFactory.getClient().get("total"));
		System.out.print(",");
		System.out.print(MemcacheFactory.getClient().get("k2"));
		System.out.print(",");
		System.out.print(MemcacheFactory.getClient().get("k1"));
		System.out.print(",");
		System.out.print(MemcacheFactory.getClient().get("on"));
		System.out.println();
	}
}///~;
