-libraryjars  <java.home>/lib/rt.jar 
-libraryjars  <java.home>/lib/jce.jar 
-libraryjars  <java.home>/lib/jsse.jar 


#-obfuscationdictionary dictionaries/compact.txt 
#-classobfuscationdictionary dictionaries/shakespeare.txt 

# -printmapping proguard.map 
-overloadaggressively 
-defaultpackage '' 
# -flattenpackagehierarchy '' 
# -dontusemixedcaseclassnames 
# -keeppackagenames 
-allowaccessmodification 
-dontoptimize 
# 因为项目中使用到了jpa的Annotation注解，需要保留这个属性 
-keepattributes *Annotation* 

# Keep entity classes extends java.io.Serializable 
# 保留jpa中使用到的所有实体类，不进行混淆 
-keep public class * implements java.io.Serializable{ 
public protected private *; 
} 

-keep public class com.ray.communicate.server.FireSocketServer {public protected *;}
-keep public class com.ray.communicate.server.GameClient {public protected *;}
-keep public class com.ray.communicate.client.socket.MessageListener{public protected *;}
-keep public class com.ray.communicate.message.* {public protected *;} 
-keep public class com.ray.communicate.server.logic.FireProtocolCodecFactory {public protected *;}
-keep public class com.ray.communicate.server.ssl.BogusSslContextFactory {public protected *;}
-keep public class com.ray.communicate.client.ClientSupport {public protected *;}
-keep public class com.ray.communicate.client.FireClient {public protected *;}
-keep public class com.ray.communicate.client.FireSocketClient {public protected *;}
-keep public class com.ray.communicate.client.socket.SocketClient {public protected *;}

-keep public class com.ray.fire.util.Log { 
    public protected *; 
}

-keep public class com.ray.communicate.FSystem { 
    public protected *; 
}

-keep public class com.ray.communicate.FSetting { 
    public protected *; 
}

-keep class org.apache.mina.**{
	*;
}

-keepattributes Exceptions

# Keep - Applications. Keep all application classes, along with their 'main' methods. 
#-keepclasseswithmembers public class * { 
#    public static void main(java.lang.String[]); 
#} 

# Keep names - Native method names. Keep all native class/method names. 
-keepclasseswithmembers,allowshrinking class * { 
    native <methods>; 
} 