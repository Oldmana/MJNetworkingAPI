package net.teambrimis.brett.MJNetworkingAPI.server;

import java.io.IOException;
import java.net.ServerSocket;

import net.teambrimis.brett.MJNetworkingAPI.MJConnection;

public class MJServer
{
	private ServerSocket server;
	
	public MJServer(int port) throws IOException
	{
		server = new ServerSocket(port);
	}
	
	public MJConnectAttempt listen()
	{
		MJConnection c = null;
		try
		{
			c = new MJConnection(server.accept());
		}
		catch (IOException e)
		{
			return new MJConnectAttempt(null, e);
		}
		return new MJConnectAttempt(c, null);
	}
	
	public ServerSocket getServerSocket()
	{
		return server;
	}
	
	public void close() throws IOException
	{
		server.close();
	}
	
	public class MJConnectAttempt
	{
		private MJConnection socket;
		
		private IOException error;
		
		public MJConnectAttempt(MJConnection s, IOException e)
		{
			socket = s;
			error = e;
		}
		
		public boolean successful()
		{
			return error == null;
		}
		
		public MJConnection getConnection()
		{
			return socket;
		}
		
		public IOException getError()
		{
			return error;
		}
	}
}
