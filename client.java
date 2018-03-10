import java.io.*;
import java.net.*;

class client{
	
	public static void main(String[] args) throws Exception{
		
		String option;
		
		DataInputStream in = new DataInputStream(System.in);
		Socket s = new Socket("localhost", Integer.parseInt(args[0]));
		
		System.out.println("MENU");
		System.out.println("1.SEND");
		System.out.println("2.Receive");
		System.out.println("3.ls");
		
		client ftp=new client();
		
		while(true) {
			
			option = in.readLine();
			if(option.equals("1")) {
				//for uploading the file on server
				System.out.println("SEND Command Received");
				ftp.sendfile(s);
			}
			else if(option.equals("2")) {
				//for downloading the file on server
				System.out.println("RECEIVE command received");
				ftp.receivefile(s);
			}
			else if(option.equals("3")) {
				//For listing available files on server
				System.out.println("ls command received");
				ftp.listdirectory(s);
			}
		}
	}
	
	public void sendfile(Socket s) throws Exception
	{
		Socket ssock = s;
		
		DataInputStream in = new DataInputStream(System.in);
		
		DataInputStream cin = new DataInputStream(ssock.getInputStream());
		DataOutputStream cout = new DataOutputStream(ssock.getOutputStream());
		
		cout.writeUTF("RECEIVE");
		
		String filename = in.readLine();
		System.out.println("Reading File "+filename);
		cout.writeUTF(filename);
		File f=new File(filename);
		FileInputStream fin=new FileInputStream(f);
		
		int ch;
		do {
			
			ch=fin.read();
			cout.writeUTF(String.valueOf(ch));
		}while(ch!=-1);
		
		fin.close();
		System.out.println("File Sent");
	}
	
	public void receivefile(Socket s) throws Exception
	{
		Socket ssock=s;
		DataInputStream in=new DataInputStream(System.in);
		DataInputStream cin = new DataInputStream(ssock.getInputStream());
		DataOutputStream cout = new DataOutputStream(ssock.getOutputStream());
		
		cout.writeUTF("SEND");
		
		String filename = in.readLine();
		cout.writeUTF(filename);
		System.out.println("Receiving File " + filename);
		
		File f = new File(filename);
		FileOutputStream fout = new FileOutputStream(f);
		int ch;
		
		
		do {
			ch=Integer.parseInt(cin.readUTF());
			if(ch!=-1)
				fout.write(ch);
		}while(ch!=-1);
		System.out.println("Received File...");
		fout.close();
	}
	
	public void listdirectory(Socket s) throws Exception {
		
		Socket ssock = s;
		
		DataInputStream cin = new DataInputStream(ssock.getInputStream());
		DataOutputStream cout = new DataOutputStream(ssock.getOutputStream());
		
	}
}