import java.io.*;
import java.net.*;

class server {
	
	public static void main(String[] args) throws Exception {
		
		ServerSocket Sock = new ServerSocket(Integer.parseInt(args[0]));
		Socket s = Sock.accept();
		
		DataInputStream cin = new DataInputStream(s.getInputStream());
		DataOutputStream cout = new DataOutputStream(s.getOutputStream());
		
		server ftp = new server();
		
		while(true)
		{
			String option = cin.readUTF();
			
			if(option.equals("SEND")) {
				
				System.out.println("SEND Command Received");
				ftp.sendfile(s);
			}
			else if(option.equals("RECEIVE")) {
				
				System.out.println("RECEIVE Command Received");
				ftp.receivefile(s);
			}
			else if(option.equals("LIST")) {

				System.out.println("LIST Command Received");
				ftp.listdirectory(s);
			}
			else if(option.equals("CD")) {

				System.out.println("CD Command Received");
				ftp.cd(s);
			}
		}
	}
	
	public void sendfile(Socket s) throws Exception {
		
		Socket ssock = s;
		
		DataInputStream cin = new DataInputStream(ssock.getInputStream());
		DataOutputStream cout = new DataOutputStream(ssock.getOutputStream());
		
		String filename = cin.readUTF();
		System.out.println("Reading file "+filename);
		File f = new File(filename);
		FileInputStream fin = new FileInputStream(f);
		
		int ch;
		
		do {
			
			ch = fin.read();
			cout.writeUTF(Integer.toString(ch));
		}while(ch!=-1);
		
		fin.close();
		System.out.println("File Sent");
	}
	
	public void receivefile(Socket s) throws Exception {
		
		Socket ssock = s;
		DataInputStream cin = new DataInputStream(ssock.getInputStream());
		DataOutputStream cout = new DataOutputStream(ssock.getOutputStream());
		
		String filename = cin.readUTF();
		System.out.println("Receiving file "+filename);
		File f = new File(filename);
		
		FileOutputStream fout = new FileOutputStream(f);
		int ch;
		
		while((ch=Integer.parseInt(cin.readUTF()))!=-1) {
			
			fout.write(ch);
		}
		
		System.out.println("Received file...");
		fout.close();
	}

	public void listdirectory(Socket s) throws Exception {

		Socket ssock = s;
		DataInputStream cin = new DataInputStream(ssock.getInputStream());
		DataOutputStream cout = new DataOutputStream(ssock.getOutputStream());

		File folder = new File(".");
		File[] listOfFiles = folder.listFiles();

    	for (int i = 0; i < listOfFiles.length; i++) {

			cout.writeUTF(listOfFiles[i].getName());
    	}
    	cout.writeUTF("EOF");
	}

	public void cd(Socket s) throws Exception {

		Socket ssock = s;
		DataInputStream cin = new DataInputStream(ssock.getInputStream());
		DataOutputStream cout = new DataOutputStream(ssock.getOutputStream());

		String path = cin.readUTF();

		File file=new File(".");
      	String oldpath = file.getAbsolutePath();
      	oldpath = oldpath.substring(0, oldpath.length() - 1);
      	System.setProperty("user.dir", oldpath+path);

      	cout.writeUTF("OK");
	}
}