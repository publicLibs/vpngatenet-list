# vpngatenet-list


[![](https://jitpack.io/v/publicLibs/vpngatenet-list.svg)](https://jitpack.io/#publicLibs/vpngatenet-list)

## Usage:

```java

import com.github.publiclibs.vpngatenet.list.data.VpnConf;
import com.github.publiclibs.vpngatenet.list.utils.VpnConfUtils;
import com.github.publiclibs.vpngatenet.list.utils.VpnGateNetFetcher;

Stream<VpnConf> sortedBySpeed = VpnGateNetFetcher.fetchFast();
Stream<VpnConf> sortedBySpeedLimit3 = VpnGateNetFetcher.fetchFast(3);
Stream<VpnConf> fastStreamUSALimit10 = VpnGateNetFetcher.fetchFastCountry(10, "US");

//How to get ovpn config file?
VpnConf conf =...
String fullOvpnConf= VpnConfUtils.toOvpnConf(conf);

fastStreamX.forEachOrdered((final VpnConf conf) -> System.err.println(VpnConfUtils.toOvpnConf(conf)));

//and more 
//see com.github.publiclibs.vpngatenet.list.utils.VpnGateNetFetcher
//see com.github.publiclibs.vpngatenet.list.utils.VpnConfStreams
```



## Use as maven dependency:

```xml
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
```

```xml
<dependency>
	<groupId>com.github.publicLibs</groupId>
	<artifactId>vpngatenet-list</artifactId>
	<version>main-SNAPSHOT</version>
</dependency>
```
