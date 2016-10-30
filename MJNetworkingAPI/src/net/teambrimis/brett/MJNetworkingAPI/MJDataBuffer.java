package net.teambrimis.brett.MJNetworkingAPI;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

/**A wrapper for the ByteBuffer class. Provides utilities to simplify adding common variables sent in packets.
 * 
 */
public class MJDataBuffer
{
	private ByteBuffer bb;
	
	public MJDataBuffer()
	{
		bb = ByteBuffer.allocate(128);
	}
	
	public MJDataBuffer(int estimatedSize)
	{
		bb = ByteBuffer.allocate(estimatedSize);
	}
	
	public MJDataBuffer(byte[] data)
	{
		bb = ByteBuffer.wrap(data);
	}
	
	public void addBoolean(boolean b)
	{
		try
		{
			bb.put((byte) (b ? 1 : 0));
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 1);
			addBoolean(b);
		}
	}
	
	public boolean getBoolean()
	{
		return bb.get() == 1;
	}
	
	public void addByte(byte b)
	{
		try
		{
			bb.put(b);
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 1);
			addByte(b);
		}
	}
	
	public byte getByte()
	{
		return bb.get();
	}
	
	public void addShort(short s)
	{
		try
		{
			bb.putShort(s);
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 2);
			addShort(s);
		}
	}
	
	public short getShort()
	{
		return bb.getShort();
	}
	
	public void addInt(int i)
	{
		try
		{
			bb.putInt(i);
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 4);
			addInt(i);
		}
	}
	
	public int getInt()
	{
		return bb.getInt();
	}
	
	public void addLong(long l)
	{
		try
		{
			bb.putLong(l);
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 8);
			addLong(l);
		}
	}
	
	public long getLong()
	{
		return bb.getLong();
	}
	
	public void addFloat(float f)
	{
		try
		{
			bb.putFloat(f);
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 4);
			addFloat(f);
		}
	}
	
	public float getFloat()
	{
		return bb.getFloat();
	}
	
	public void addDouble(double d)
	{
		try
		{
			bb.putDouble(d);
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 8);
			addDouble(d);
		}
	}
	
	public double getDouble()
	{
		return bb.getDouble();
	}
	
	public void addChar(char c)
	{
		try
		{
			bb.putChar(c);
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 2);
			addChar(c);
		}
	}
	
	public char getChar()
	{
		return bb.getChar();
	}
	
	public void addString(String s)
	{
		addInt(s.length());
		for (char c : s.toCharArray())
		{
			try
			{
				addChar(c);
			}
			catch (BufferOverflowException e)
			{
				resize(bb.capacity() + 2);
				addChar(c);
			}
		}
	}
	
	public String getString()
	{
		int len = getInt();
		String str = "";
		for (int i = 0 ; i < len ; i++)
		{
			str += getChar();
		}
		return str;
	}
	
	public void addInts(int[] is)
	{
		addInt(is.length);
		for (int i : is)
		{
			addInt(i);
		}
	}
	
	public int[] getInts()
	{
		int len = getInt();
		int[] is = new int[len];
		for (int i = 0 ; i < len ; i++)
		{
			is[i] = getInt();
		}
		return is;
	}
	
	private void finalizeArray()
	{
		try
		{
			bb.put((byte) -232);
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 1);
			bb.put((byte) -232);
		}
		try
		{
			bb.put((byte) 242);
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 1);
			bb.put((byte) 242);
		}
	}
	
	private boolean arrayFinished()
	{
		int pos = bb.position();
		if (bb.get() == -232 && bb.get() == 242)
		{
			bb.position(pos);
			return true;
		}
		bb.position(pos);
		return false;
	}
	
	public void finalizeData()
	{
		try
		{
			bb.put((byte) -242);
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 1);
			bb.put((byte) -242);
		}
		try
		{
			bb.put((byte) 232);
		}
		catch (BufferOverflowException e)
		{
			resize(bb.capacity() + 1);
			bb.put((byte) 232);
		}
		resize(bb.position());
	}
	
	public void resize(int newCapacity)
	{
		ByteBuffer resized = ByteBuffer.allocate(newCapacity);
		resized.put(bb.array());
		bb = resized;
	}
	
	public byte[] getByteArray()
	{
		return bb.array();
	}
}
