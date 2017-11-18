//This class contains implementations of methods to 
//   -- pack an integer into 4 consecutive bytes of a byte array
//   -- unpack an integer from 4 consecutive bytes of a byte array
//   -- exhaustively test the pack and unpack methods.
// 
// This file should be saved as PackableMemory.java.  Once it has been
//  compiled, the tester can be invoked by typing "java PackableMemory"

import java.io.*;
import java.lang.Object;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

class PackableMemory
{
   int size; 
   public byte mem[] = null;
   int bm[];
   int mask[];
   OpenFileTable table = new OpenFileTable();
   
   public PackableMemory(int size)
   {
      this.size = size;
      this.mem = new byte[size];
      bm = new int[2];
      mask = new int[32];
      int s = 31;
      for(int i = 0;i<32;i++)
      {
    	  mask[i] = 1<<s;
    	  s = s-1;
      }
      
   }

   // Pack the 4-byte integer val into the four bytes mem[loc]...mem[loc+3].
   // The most significant porion of the integer is stored in mem[loc].
   // Bytes are masked out of the integer and stored in the array, working
   // from right(least significant) to left (most significant).
   void pack(int val, int loc)
   {
      final int MASK = 0xff; //255
      for (int i=3; i >= 0; i--)
      {
         mem[loc+i] = (byte)(val & MASK);
         val = val >> 8;
      }
   }

   // Unpack the four bytes mem[loc]...mem[loc+3] into a 4-byte integer,
   //  and return the resulting integer value.
   // The most significant porion of the integer is stored in mem[loc].
   // Bytes are 'OR'ed into the integer, working from left (most significant) 
   //  to right (least significant)
   int unpack(int loc)
   {
      final int MASK = 0xff;
      int v = (int)mem[loc] & MASK;
      for (int i=1; i < 4; i++)
      {
         v = v << 8; 
         v = v | ((int)mem[loc+i] & MASK);
      }
      return v;
   }



   // Test the above pack and unpack methods by iterating the following
   //  over all possible 4-byte integers: pack the integer,
   //  then unpack it, and then verify that the unpacked integer equals the
   //  original integer.  It tests all nonnegative numbers in ascending order
   //  and then all negative numbers in ascending order.  The transition from
   //  positive to negative numbers happens implicitly due to integer overflow.
   public void packTest()
   {
 
      int i = 0;
      long k = 0;
      do 
      {
         this.pack(i,4);
         int j = this.unpack(4);
         if (j != i)
         {
            System.out.printf("pack/unpack test failed: i = %d, j = %d\n",i,j);
            System.exit(0);
         }
         i++; k++;
      }
      while (i != 0);
      System.out.printf("pack/unpack test successful, %d iterations\n",k);
   }
   
   public int flip(int i)
   {
	   return ~i;
   }
   
   public void set_bit(int i)
   {
	   if(i<32)
	   {
		   bm[0] = bm[0]|mask[i];
	   }
	   else if(i<64)
	   {
		   bm[1] = bm[1]|mask[i-32];
	   }
   }
   
   public void reset_bit(int i)
   {
	   if(i<32)
	   {
		   bm[0] = bm[0] & flip(mask[i]);
	   }
	   else if(i<64)
	   {
		   bm[1] = bm[1]&flip(mask[i-32]);
	   }
   }
   
   //finds the next free block
   public int find_next_block()
   {
	   int[] block = new int[24*3];
	   int index = 0;
	   for(int i =1;i<7;i++)
	   {
		   for(int k= 0;k<4;k++)
		   {
			   for(int n=1;n<4;n++)
			   {
				   block[index] = this.unpack( (64*i)+(k*16)+(n*4) );
				   index +=1;
			   }
		   }
	   }
	   
	   int max = -1;
	   for(int i = 0;i<block.length;i++)
	   {
		   if(block[i]>max)
			   max = block[i];
	   }
	   return max+1;
   }
   
   public int get_file(String file_name)
   {
	   //gets the first index to file in directory
	   for(int i=0;i<8;i++)
	   {
		   String name = "";
		   if(this.unpack((this.unpack(64+4)*64)+(i*8))!=0)
		   {
			   for(int n=0;n<4;n++)
			   {
				   char c = (char)mem[(this.unpack(64+4)*64)+(i*8)+n]; //check
				   if(c!=0)
					   name += Character.toString(c);
			   }
		   }
		   if(name.compareTo(file_name)==0)
			   return (this.unpack(64+4)*64)+(i*8);
	   }
	   //check if directory is allocated
	   if(this.unpack(64+4+4)!=-1)
	   {
		   for(int i=0;i<8;i++)
		   {
			   String name = "";
			   if(this.unpack((this.unpack(64+4+4)*64)+(i*8))!=0)
			   {
				   for(int n=0;n<4;n++)
				   {
					   char c = (char)mem[(this.unpack(64+4+4)*64)+(i*8)+n]; //check
					   if(c!=0)
						   name += Character.toString(c);
				   }
			   }
			   if(name.compareTo(file_name)==0)
				   return (this.unpack(64+4+4)*64)+(i*8);
		   }
	   }
	   if(this.unpack(64+4+4+4)!=-1)
	   {
		   for(int i=0;i<8;i++)
		   {
			   String name = "";
			   if(this.unpack((this.unpack(64+4+4+4)*64)+(i*8))!=0)
			   {
				   for(int n=0;n<4;n++)
				   {
					   char c = (char)mem[(this.unpack(64+4+4+4)*64)+(i*8)+n]; //check
					   if(c!=0)
						   name += Character.toString(c);
				   }
			   }
			   if(name.compareTo(file_name)==0)
				   return (this.unpack(64+4+4+4)*64)+(i*8);
		   }
	   }
	   return 0;
   }
   
   public void create(String file_name)
   {
	   /*also implement if file already exists*/
	   if(file_name.length()<5&&file_name.length()>0)
	   {
	   for(int i = 1;i<24;i++) // is fd index
	   {
		   if(this.unpack(64+(i*16))==-1)
		   {
			   if(this.get_file(file_name)!=0)
			   {
				   System.out.println("error"); //duplicate file
				   return;
			   }
			   if(this.find_next_block()>63)
			   {
				   System.out.println("error"); //
				   return;
			   }
			   else
			   {
				   this.pack(0, (64+(i*16))); //set 0 as length in fd
				   this.pack(this.find_next_block(), (64+(i*16)+4)); //sets first block# to fd
				   System.out.printf("%s created",file_name);
				   System.out.println();
			   }
			   
			   //searches in the first directory block
			   for(int k=0;k<8;k++)
			   {
				   //find and set directory entry
				   if( this.unpack((this.unpack(64+4)*64)+(k*8)) ==0 )
				   {
					 //set name and descriptor index
					   byte name [] = file_name.getBytes();
					   for(int n=0;n<name.length;n++)
					   {
						   mem[((this.unpack(64+4)*64)+(k*8))+n] = name[n];
					   }
					   this.pack(i, ((this.unpack(64+4)*64)+(k*8))+4);
					   return;
				   }

			   }

			   //goes to the 2nd directory block
			   if(this.unpack(64+4+4)==-1)
			   {
				   //expands the directory fd
				   this.pack(128, 64);
				   this.pack(this.find_next_block(), 64+4+4);
				   for(int j =0;j<8;j++) //sets each name of dr to 0
				   {
					   //sets the name of all directory file names to 0
					   this.pack(0, ((this.unpack(64+4+4)*64)+(j*8)));
				   }
			   }   
			   for(int k=0;k<8;k++)
			   {
				   //searches the second directory block
				   if( (this.unpack((this.unpack(64+4+4)*64)+(k*8 )) ) == 0 )
				   {
					   //set name and descriptor entry
					   byte name [] = file_name.getBytes();
					   for(int n=0;n<name.length;n++)
					   {
						   mem[((this.unpack(64+4+4)*64)+(k*8))+n] = name[n];
					   }
					   this.pack(i, ((this.unpack(64+4+4)*64)+(k*8))+4);
					   return; 
				   }

			   }

			   //goes to the 3rd directory
			   if(this.unpack(64+4+4+4)==-1)
			   {
				   //expands the directory fd
				   this.pack(192, 64);
				   this.pack(this.find_next_block(), 64+4+4+4);
				   for(int n =0;n<8;n++) //sets each name of dr to 0
				   {
					   //sets the name of all directory file names to 0
					   this.pack(0, (this.unpack(64+4+4+4)*64)+(n*8));
				   }
			   } 

			   for(int k=0;k<8;k++)
			   {
				   //searches the 3rd directory block
				   if( (this.unpack((this.unpack(64+4+4+4)*64) + (k*8)) ) == 0 )
				   {
					   byte name [] = file_name.getBytes();
					   for(int n=0;n<name.length;n++)
					   {
						   mem[((this.unpack(64+4+4+4)*64)+(k*8))+n] = name[n];
					   }
					   this.pack(i, ((this.unpack(64+4+4+4)*64)+(k*8))+4);
					   return;
				   }

			   }
			   
			   break; //for insurance
		   }
	   }
	   //System.out.println("No more file descriptors");
	   }
	   else
	   {
		   System.out.println("error"); //Invalid File Name
	   }
   }
   
   public void destroy(String file_name)
   {
	   /*update bitmap*/
	   /*check to see if open*/
	   //checks to see if file_name exist
	   int index = this.get_file(file_name);
	   int fd;
	   if(index==0)
	   {
		   System.out.println("error");
		   return;
	   }
	   else
	   {
		   fd = this.unpack(index+4); //is the first index to fd
		   this.pack(0, index); //frees directory entry
		   this.pack(0, index+4); //might cause error sets fd in empty file to 0
		   
		   //free blocks in the fd
		   for(int i=0;i<64;i++)
		   {
			   if(this.unpack(64+(fd*16)+4)!=-1)
			   {
				   mem[(this.unpack(64+(fd*16)+4)*64)+i] = 0; 
			   }
			   if(this.unpack(64+(fd*16)+4+4)!=-1)
			   {
				   mem[(this.unpack(64+(fd*16)+4+4)*64)+i] = 0;
			   }
			   if(this.unpack(64+(fd*16)+4+4+4)!=-1)
			   {
				   mem[(this.unpack(64+(fd*16)+4+4+4)*64)+i] = 0;
			   }
		   }
		   this.pack(-1,64+(fd*16));
		   this.pack(-1,64+(fd*16)+4);
		   this.pack(-1,64+(fd*16)+4+4);
		   this.pack(-1,64+(fd*16)+4+4+4);
	   }
	   
	   
   }
   
   public int open(String file_name)
   {
	   //check if already open
	   int index = this.get_file(file_name);
	   int fd = this.unpack(index+4);
	   if(table.is_open(fd))
	   {
		   System.out.println("error");
		   return -1;
	   }
		   
	   //check for available entry
	   if(table.Entry==4)
	   {
		   System.out.println("error");
		   return -1;
	   }
	   
	   //fill oft buffer, current position, fd, length
	   int oft_index = table.find_entry();
	   for(int i=0;i<64;i++)
	   {
		   table.oft[(oft_index*76)+i] = this.mem[(this.unpack(64+(fd*16)+4)*64)+i];
	   }
	   table.pack(0, (oft_index*76)+64);
	   table.pack(fd, (oft_index*76)+64+4);
	   table.pack(this.unpack(64+(fd*16)), (oft_index*76)+64+4+4);
	   table.inc();
	   
	   System.out.printf("%s opened %d", file_name, oft_index);
	   System.out.println();
	   return oft_index;
   }

   public int close(int index)
   {
	   //check if oft index entry is open
	   if(table.unpack((index*76)+64)==-1)
	   {
		   System.out.println("error");
		   return -1;
	   }
	   
	   //writes the oft buffer back to the ldisk based on current
	   int fd = table.unpack((index*76)+64+4);
	   if(table.unpack((index*76)+64)<64) //bug
	   {
		   for(int i=0;i<64;i++)
		   {
			   this.mem[(this.unpack(64+(fd*16)+4)*64)+i] = table.oft[(index*76)+i];
		   }
	   }
	   if(table.unpack((index*76)+64)>63) //bug
	   {
		   for(int i=0;i<64;i++)
		   {
			   this.mem[(this.unpack(64+(fd*16)+4+4)*64)+i] = table.oft[(index*76)+i];
		   }
	   }
	   if(table.unpack((index*76)+64)>127) //bug
	   {
		   for(int i=0;i<64;i++)
		   {
			   this.mem[(this.unpack(64+(fd*16)+4+4+4)*64)+i] = table.oft[(index*76)+i];
		   }
	   }
	   this.pack(table.unpack(index*76+(64+4+4)), 64+(fd*16)); //update file length
	   table.pack(-1, (index*76)+64);
	   table.pack(-1, (index*76)+64+4);
	   table.pack(-1, (index*76)+64+4+4);
	   for(int i=0;i<64;i++)
	   {
		   table.oft[(index*76)+i] = 0;
	   }
	   table.dec();
	   System.out.printf("%d closed",index);
	   System.out.println();
	   return 1;
   }
   
   public String read(int index, int count) //returns bytes read
   {
	   //check valid oft index
	   if(index>3||index<1)
	   {
		   System.out.println("error");
		   return "error";
	   }
	   //check if oft is open
	   if(table.unpack((index*76)+64) ==-1)
	   {
		   System.out.println("error");
		   return "error";
	   }
		   
	   String bytes_read = "";
	   int current = table.unpack((index*76)+64);
	   int fd = table.unpack((index*76)+64+4);
	   int length = table.unpack((index*76)+64+4+4);
	   int block = (current/64)+1;
	   
	   for(int i=0;i<count;i++)
	   {
		   if((current<length&&current==0)||(current<length&&((current%64)!=0)))
		   {
			   //read byte
			   char c = (char) table.oft[(index*76)+(current%64)];
			   bytes_read += Character.toString(c);
			   
		   }
		   else if(current<length&&current!=0&&((current%64)==0))
		   {
			   //write buffer to ldisk
			   for(int n=0;n<64;n++)
			   {
				   this.mem[(this.unpack(64+(fd*16)+((((current/64)+1)-1)*4))*64)+n] = table.oft[(index*76)+n];
			   }
			   //copy next block to oft buffer
			   for(int n=0;n<64;n++)
			   {
				   table.oft[(index*76)+n] = this.mem[(this.unpack(64+(fd*16)+(((current/64)+1)*4))*64)+n];
			   }
			   //read byte
			   char c = (char) table.oft[(index*76)+(current%64)];
			   bytes_read += Character.toString(c); //bug here
		   }
		   else
		   {

			   //update oft current
			   table.pack(current, (index*76)+64);
			   System.out.println(bytes_read);
			   return bytes_read;
		   }
		   current +=1;
	   }
	   table.pack(current, (index*76)+64);
	   System.out.println(bytes_read);
	   return bytes_read;
   }
   
   public int write(int index, char c, int count) //return #bytes written
   {	//check if oft index is valid  
	   if(index>3||index<1)
	   {
		   System.out.println("Error");
		   return -1;
	   }
	   
	   //check if oft is open
	   if(table.unpack((index*76)+64) ==-1)
	   {
		   System.out.println("Error");
		   return -1;
	   }
	   
	   int written = 0;
	   int current = table.unpack((index*76)+64);
	   int fd = table.unpack((index*76)+64+4);
	   int length = table.unpack((index*76)+64+4+4);
	   int block = (current/64)+1; //change all to ((current/64)+1)
	   
	   for(int i=0;i<count;i++)
	   {
		   if(current==0||(current<192&&((current%64)!=0)))
		   {
			   	if(table.oft[(index*76)+(current%64)]==0)
			   	{
			   		length +=1;
			   	}
			   	table.oft[(index*76)+(current%64)] = (byte) c;
		   }
		   else if(current<192&&current!=0&&((current%64)==0))
		   {
			   //write buffer to ldisk
			   for(int n=0;n<64;n++)
			   {
				   this.mem[(this.unpack(64+(fd*16)+((((current/64)+1)-1)*4))*64)+n] = table.oft[(index*76)+n];
			   }
			   //check if next block is available
			   if(this.unpack(64+(fd*16)+(((current/64)+1)*4))==-1 )
			   {
				   //check if can allocate next block
				   int next_block = this.find_next_block();
				   if(next_block>63)
				   {
					   System.out.println("Error"); //no more blocks to allocate
					   return written;
				   }
				   //update fd block#
				   this.pack(next_block,64+(fd*16)+(((current/64)+1)*4)); //fault
				   //copy new block to oft buffer
				   for(int n=0;n<64;n++)
				   {
					   table.oft[(index*76)+n] = this.mem[(this.unpack(64+(fd*16)+(((current/64)+1)*4))*64)+n];
				   }
				   //write char to buffer
				   int stop = 0; //for testing
				   table.oft[(index*76)+(current%64)] = (byte) c;
				   length +=1;
			   }
			   else
			   {
				 //copy new block to oft buffer
				   for(int n=0;n<64;n++)
				   {
					   table.oft[(index*76)+n] = this.mem[(this.unpack(64+(fd*16)+(((current/64)+1)*4))*64)+n];
				   }
				 //write char to buffer
				   table.oft[(index*76)+(current%64)] = (byte) c;
				   if(table.oft[(index*76)+(current%64)]==0)
				   	{
				   		length +=1;
				   	}
			   }
		   }
		   else //end of file 192
		   {
			   //copy buffer back to ldisk
			   for(int n=0;n<64;n++)
			   {
				   this.mem[(this.unpack(64+(fd*16)+((((current/64)+1)-1)*4))*64)+n] = table.oft[(index*76)+n];
			   }
			   //update current and length in oft
			   table.pack(current, (index*76)+64);
			   table.pack(length, (index*76)+64+4+4);
			   //update length in descriptor
			   this.pack(length, 64+(fd*16));
			   System.out.printf("%d bytes written", written);
			   System.out.println();
			   return written;
		   }
			   
			   
			   
		   current +=1;
		   written +=1;
	   }
	   //update current and length in the oft entry
	   table.pack(current, (index*76)+64);
	   table.pack(length, (index*76)+64+4+4);
	   //update length in descriptor
	   this.pack(length, 64+(fd*16));
	   //copy buffer back to ldisk
	   for(int n=0;n<64;n++)
	   {
		   this.mem[(this.unpack(64+(fd*16)+((((current/64)+1))*4))*64)+n] = table.oft[(index*76)+n];
	   }
	   System.out.printf("%d bytes written", written);
	   System.out.println();
	   return written;
   }
   
   public void lseek(int index, int pos)
   {
	   int current = table.unpack((index*76)+64);
	   int fd = table.unpack((index*76)+64+4);
	   int length = table.unpack((index*76)+64+4+4);
	   int block = (current/64)+1;
	   
	   if(pos>length)
	   {
		   System.out.println("error");
		   return;
	   }
	   
	   if(block!=((pos/64)+1))
	   {
		   //write oft buffer back to ldisk
		   for(int n=0;n<64;n++)
		   {
			   this.mem[(this.unpack(64+(fd*16)+(block*4))*64)+n] = table.oft[(index*76)+n];
		   }
		   //write new block to oft buffer
		   for(int n=0;n<64;n++)
		   {
			   table.oft[(index*76)+n] = this.mem[(this.unpack(64+(fd*16)+(((pos/64)+1)*4))*64)+n];
		   }
	   }
	   
	   //update oft current
	   current = pos;
	   table.pack(current, (index*76)+64);
	   System.out.printf("position is %d", current);
	   System.out.println();
   }
   
   public String directory() /*returned string[]*/
   {
	   //read the directory blocks and return a string[] of file names
	   String answer = "";
	   for(int i = 0;i<8;i++)
	   {
		   //goes through the first directory block
		   if( this.unpack((this.unpack(64+4)*64)+(i*8)) != 0)
		   {
			   for(int k=0;k<4;k++)
			   {
				   if(mem[(this.unpack(64+4)*64)+(i*8)+k]==0)
				   {
					   break;
				   }
				   else
				   {
					   char c = (char) mem[(this.unpack(64+4)*64)+(i*8)+k];
					   answer += Character.toString(c);
				   }
				   
			   }
			   answer += " ";
		   }
	   }
	   if(this.unpack(64+4+4)!=-1)
	   {
		   for(int i = 0;i<8;i++)
		   {
			   if( this.unpack((this.unpack(64+4+4)*64)+(i*8)) != 0)
			   {
				   for(int k=0;k<4;k++)
				   {
					   if(mem[(this.unpack(64+4+4)*64)+(i*8)+k]==0)
					   {
						   break;
					   }
					   else
					   {
						   char c = (char) mem[(this.unpack(64+4+4)*64)+(i*8)+k];
						   answer += Character.toString(c);
					   }
				   }
				   answer += " ";
			   }
		   }
	   }
	   if(this.unpack(64+4+4+4)!=-1)
	   {
		   for(int i = 0;i<8;i++)
		   {
			   if( this.unpack((this.unpack(64+4+4+4)*64)+(i*8)) != 0)
			   {
				   for(int k=0;k<4;k++)
				   {
					   if(mem[(this.unpack(64+4+4+4)*64)+(i*8)+k]==0)
					   {
						   break;
					   }
					   else
					   {
						   char c = (char) mem[(this.unpack(64+4+4+4)*64)+(i*8)+k];
						   answer += Character.toString(c);
					   }
				   }
				   answer += " ";
			   }
		   }
	   }

	   return answer.substring(0, answer.length()-1);
   }
   
   public void init(String... file) //was String file
   {
	   if(file.length==0)
	   {
		   System.out.println("disk initialized");
		   for(int i = 0;i<8;i++)
		   {
			   this.set_bit(i); //check later
		   }
		   this.pack(bm[0],0);
		   for(int i = 0;i<24;i++)
		   {
			   //puts -1 to all slots in every descriptor
			   this.pack(-1,64+(i*16)); 
			   this.pack(-1,64+(i*16)+4);
			   this.pack(-1,64+(i*16)+8);
			   this.pack(-1,64+(i*16)+12);
		   }
		   this.pack(64,64); //directory descriptor is 64 bytes
		   this.pack(7,64+4); //puts 7 as directory block number

		   for(int i =0;i<8;i++) //sets each name of dr to 0
		   {
			   //sets the name of all directory file names to 0
			   this.pack(0, (this.unpack(64+4)*64)+(i*8));
		   }
		   
		   /*put directory in oft*/
		   
		   table.inc();
		   for(int n=0;n<64;n++)
		   {
			   table.oft[n] = mem[(this.unpack(64+4)*64)+n];
		   }
		   table.pack(0,64);
		   table.pack(0,64+4);
		   table.pack(this.unpack(64), 64+4+4);
	   }
	   else if(file.length == 1)
	   {
		   System.out.println("disk restored");
		   //put directory in oft*
		   table.inc();
		   for(int n=0;n<64;n++)
		   {
			   table.oft[n] = mem[(this.unpack(64+4)*64)+n];
		   }
		   table.pack(0,64);
		   table.pack(0,64+4);
		   table.pack(this.unpack(64), 64+4+4);
		   /*change for testing*/
		   String full_dest = "C:"+File.separator+"Users"
		   +File.separator+"Jesse"+File.separator+"Desktop"
				   +File.separator+"143b"+File.separator+file[0];
		   
		   //if file does not exist create new file
		   Path fileLocation  = Paths.get(full_dest);

		   try 
		   {
			   this.mem = Files.readAllBytes(fileLocation);
		   }
		   catch (IOException e)
		   {
			   System.out.println(e);
			   //this.init();//
		   }
	   }
   }
   
   public void save(String dest) //was File dest
   {
	   //close all files
	   table = new OpenFileTable();
	   /*change for testing*/
	   String full_dest = "C:"+File.separator+"Users"
	   +File.separator+"Jesse"+File.separator+"Desktop"
			   +File.separator+"143b"+File.separator+dest;
	   try {
           Path path = Paths.get(full_dest);
           Files.write(path, this.mem);
       } catch (IOException e) {
           e.printStackTrace();
       }
	   System.out.println("disk saved");

   }
   
   // main routine to test the PackableMemory class by running the 
   //  packTest() method.
   public static void main(String[] args)
   {
      PackableMemory pm = new PackableMemory(64*64);
      
      System.out.printf("enter file to test(full path): ");
      Scanner inputReader = new Scanner(System.in);
      String input = inputReader.nextLine();
      int input_length = input.length();
      //change for testing
      //System.out.println(input.substring(0, input_length-"input.txt".length()+1) + "75288648.txt");
      String out_dest = input.substring(0, input_length-"input.txt".length()) + "75288648.txt";//"C:"+File.separator+"Users"
      		 //  +File.separator+"Jesse"+File.separator+"Desktop"
   		   //+File.separator+"143b"+File.separator+"output.txt";
         try {
         PrintStream out = new PrintStream(new FileOutputStream(out_dest));
         System.setOut(out);
         }
         catch (IOException e){
       	  
         }
         
      File f = new File(input);
      try {
      FileReader fr = new FileReader(f);
      BufferedReader br = new BufferedReader(fr);
      String strLine;
      //Read File Line By Line
      while ((strLine = br.readLine()) != null)   {
        // Print the content on the console
        String cmd[] = strLine.split(" ");
        if(cmd[0].compareTo("cr")==0)
        {
        	pm.create(cmd[1]);
        }
        else if(cmd[0].compareTo("de")==0)
        {
        	pm.destroy(cmd[1]);
        }
        else if(cmd[0].compareTo("op")==0)
        {
        	pm.open(cmd[1]);
        }
        else if(cmd[0].compareTo("cl")==0)
        {
        	pm.close(Integer.parseInt(cmd[1]));
        }
        else if(cmd[0].compareTo("rd")==0)
        {
        	pm.read(Integer.parseInt(cmd[1]),Integer.parseInt(cmd[2]));
        }
        else if(cmd[0].compareTo("wr")==0)
        {
        	pm.write(Integer.parseInt(cmd[1]),cmd[2].charAt(0),Integer.parseInt(cmd[3]));
        }
        else if(cmd[0].compareTo("sk")==0)
        {
        	pm.lseek(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]));
        }
        else if(cmd[0].compareTo("dr")==0)
        {
        	System.out.println(pm.directory());
        }
        else if(cmd[0].compareTo("in")==0)
        {
        	if(cmd.length>1)
        	{
        		pm.init(cmd[1]);
        	}
        	else
        	{
        		pm.init();
        	}
        }
        else if(cmd[0].compareTo("sv")==0)
        {
        	pm.save(cmd[1]);
        }
        else
        {
        	System.out.println();
        }
      }
      br.close();
      fr.close();
      }
      catch (IOException e) {
    	  e.printStackTrace();
      }
      


      
      /*pm.init();
      pm.create("foo");
      pm.open("foo");
      pm.write(1,'x',60);
      pm.write(1,'y',10);
      pm.lseek(1,55);
      pm.read(1, 10);
      System.out.println(pm.directory());
      pm.save("disk0.txt");
      pm.init("disk0.txt");
      pm.open("foo");
      pm.read(1, 3);
      pm.create("foo");
      pm.close(1);
      System.out.println(pm.directory());*/
      //pm.create_file("C:\\Users\\Jesse\\Desktop\\143b\\t.txt");
      //pm.init("C:\\Users\\Jesse\\Desktop\\143b\\t.txt");
      
      /*pm.close(1);
      pm.open("foo");
      pm.read(1, 3);
      pm.create("foo");
      pm.close(1);
      System.out.println(pm.directory());
*/
      //System.out.printf("%d",pm.unpack(64)); //"0x%08X",pm.unpack(1)
      //pm.packTest();
      System.exit(0);
   }
}
