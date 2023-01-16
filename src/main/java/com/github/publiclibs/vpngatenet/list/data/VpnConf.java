/**
 *
 */
package com.github.publiclibs.vpngatenet.list.data;

/**
 * @author freedom1b2830
 * @date 2023-января-13 17:52:35
 */
public class VpnConf {
	// site-start
	public int ping;

	public int score;

	public int numVpnSessions;

	public String uptime;

	public int totalUsers;

	public long totalTraffic;

	public String logType;

	public String operator;

	public String message;

	public String id;

	public String countryLong;
	// site-end

	public String countryShort;

	public long speed;

	public int verb;

	public boolean client;

	public boolean persisttun;

	public String cipher;

	public boolean persistkey;

	public String resolvretry;

	public String auth;

	public boolean nobind;

	public String ca;
	public String dev;
	public Proto proto;
	public String cert;
	public String privKey;

	public String host;

	public int port;

	public int getPing() {
		return ping;
	}

	public long getSpeed() {
		return speed;
	}

}
