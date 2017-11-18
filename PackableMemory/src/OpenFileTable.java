
public class OpenFileTable {
	public byte oft[] = null;
	public int Entry = 0;
	
	public OpenFileTable()
	{
		oft = new byte[4*(64+4+4+4)];
		for(int i=1;i<4;i++)
		{
			this.pack(-1, (i*76)+64);
			this.pack(-1, (i*76)+64+4);
			this.pack(-1, (i*76)+64+4+4);
		}
	}
	
	public int find_entry()
	{
		//returns oft index of first available entry returns -1 if full
		if(this.Entry<4)
		{
			for(int i=1;i<4;i++)
			{
				if(this.unpack((76*i)+64)==-1)
					return i;
			}
		}
		return -1;
	}
	//finds if a file is open based on file descriptor
	public boolean is_open(int fd)
	{
		for(int i=1;i<4;i++)
		{
			if(fd==this.unpack(68+(76*i)))
				return true;
		}
		return false;
	}
	
	public void inc()
	{
		Entry += 1;
	}
	
	public void dec()
	{
		Entry -= 1;
	}
	
	void pack(int val, int loc)
	{
	    final int MASK = 0xff; //255
	    for (int i=3; i >= 0; i--)
	    {
	       oft[loc+i] = (byte)(val & MASK);
	       val = val >> 8;
	    }
	 }

	 int unpack(int loc)
	 {
	    final int MASK = 0xff;
	    int v = (int)oft[loc] & MASK;
	    for (int i=1; i < 4; i++)
	    {
	       v = v << 8; 
	       v = v | ((int)oft[loc+i] & MASK);
	    }
	    return v;
	 }
	
}
