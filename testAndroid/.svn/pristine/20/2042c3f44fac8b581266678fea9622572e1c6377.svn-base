package com.gorillalogic.monkeyconsole.connect;

public class ConnectionInfo {
	private ConnectionTypesEnum connectionType=null;
	private String host=null;
	private int port=-1;
	private String identifier=null;
	private String name=null;
	
	public ConnectionInfo(ConnectionTypesEnum connectionType,
			String host, int port, String identifier) {
		this.connectionType = connectionType;
		this.host = host;
		this.port = port;
		this.identifier = identifier;
	}
	
	public ConnectionTypesEnum getConnectionType() {
		return connectionType;
	}
	public void setConnectionType(ConnectionTypesEnum connectionType) {
		this.connectionType = connectionType;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getName() {
		if (name!=null && name.length()>0) {
			return name;
		}
		return getIdentifier();
	}
	public void setName(String name) {
		this.name = name;
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName()).append('(');
		sb.append("name=").append(getName()).append(' ');
		sb.append("connectionType=").append(getConnectionType().toString()).append(' ');
		sb.append("host=").append(getHost()).append(' ');
		sb.append("port=").append(getPort()).append(' ');
		sb.append("identifier=").append(getIdentifier()).append(' ');
		sb.append(')');
		return sb.toString();
	}
	
}
