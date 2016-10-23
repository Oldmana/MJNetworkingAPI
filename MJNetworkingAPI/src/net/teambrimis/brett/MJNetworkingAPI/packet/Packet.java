package net.teambrimis.brett.MJNetworkingAPI.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.teambrimis.brett.MJNetworkingAPI.MJDataBuffer;

/**The base class extended by all packets. Subclasses should override <i>fromBytes</i> and <i>toBytes</i>.
 * Additionally, every subclass must have a public static int field named <i>ID</i>, which will be the unique ID 
 * of the packet.
 *
 */
public abstract class Packet
{
	public static Map<Integer, Class<? extends Packet>> packets = new HashMap<Integer, Class<? extends Packet>>();
	
	/**Must be overridden by subclasses. Function is to restore a packet class state given the data.
	 * 
	 * @param data - Byte data
	 */
	public abstract void fromBytes(MJDataBuffer data);
	
	/**Must be overridden by subclasses. Puts fields into raw byte data.
	 *  
	 * @return A representation of the packet in bytes.
	 */
	public abstract byte[] toBytes();
	
	
	public static Packet toPacket(byte[] b)
	{
		try
		{
			MJDataBuffer data = new MJDataBuffer(b);
			Class<? extends Packet> c = packets.get((int) data.getShort());
			Packet p = c.newInstance();
			p.fromBytes(data);
			//c.getMethod("fromBytes", MJDataBuffer.class).invoke(p, data);
			return p;
		}
		catch (Exception e) {}
		return null;
	}
	
	public static Packet receivePackets(Socket s) throws Exception
	{
		s.setSoTimeout(0);
		List<Byte> data = new ArrayList<Byte>();
		DataInputStream in = new DataInputStream(s.getInputStream());
		while (!endOfPacket(data))
		{
			data.add(in.readByte());
			
			if (s.getSoTimeout() == 0)
			{
				s.setSoTimeout(10000);
			}
		}
		
		byte[] raw = new byte[data.size()];
		for (int i = 0 ; i < data.size() ; i++)
		{
			raw[i] = data.get(i);
		}
		
		return toPacket(raw);
	}
	
	private static boolean endOfPacket(List<Byte> data)
	{
		if (data.size() >= 4)
		{
			if (data.get(data.size() - 2) == -242 && data.get(data.size() - 1) == 232)
			{
				return true;
			}
		}
		return false;
		
	}
	
	public static void sendPacket(Socket s, Packet p) throws Exception
	{
		DataOutputStream out = (DataOutputStream) s.getOutputStream();
		out.write(p.toBytes());
	}
	
	public static void registerPacket(Class<? extends Packet> clazz)
	{
		try
		{
			int ID = (Integer) clazz.getDeclaredField("ID").get(null);
			packets.put(ID, clazz);
		}
		catch (Exception e)
		{
			System.err.println("Failed to register packet.. missing static ID field.");
			e.printStackTrace();
		}
	}
	
	public static Class<? extends Packet> getPacket(int ID)
	{
		return packets.get(ID);
	}
}
