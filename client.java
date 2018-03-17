import java.io.*;
import java.net.*;

class client{
	
	public static void main(String[] args) throws Exception{
		
		String option;
		
		DataInputStream in = new DataInputStream(System.in);
		Socket s = new Socket("localhost", Integer.parseInt(args[0]));
		client ftp=new client();

		System.out.println();
		System.out.println("Welcome! This is Ftp client programme. Start typing commands below.");
		
		while(true) {
		
			System.out.println();
			System.out.println();
			System.out.print(">> ");
		
			option = in.readLine();
			//tokenizing input string
			String[] command_input = option.split("\\s+");
 
			if(command_input[0].toUpperCase().equals("SEND")) {   //SEND command
				//for uploading the file on server
				ftp.sendfile(s, command_input);
			}
			else if(option.toUpperCase().equals("RECEIVE")) {
				//for downloading the file on server
				System.out.println("RECEIVE command received");
				ftp.receivefile(s);
			}
			else if(option.toUpperCase().equals("LIST")) {
				//For listing available files on server
				System.out.println("LIST command received");
				ftp.listdirectory(s);
			}
			else if(option.toUpperCase().equals("CD")) {
				//for changing directory
				System.out.println("CD command received");
				ftp.cd(s);
			}
			else if(option.toUpperCase().equals("PWD")) {
				//for getting current working directory
				System.out.println("PWD command received");
				ftp.pwd(s);
			}
		}
	}
	
	public void sendfile(Socket s, String[] command_input) throws Exception
	{
		Socket ssock = s;
		
		String filename = command_input[1];
		System.out.println("Reading File "+filename);
		File f=new File(filename);
		boolean exists = f.exists();
		if(exists == false) {				//if file does not exists return

			System.out.println("File does not exists! Please Enter valid file name.");
			return;
		}

		DataInputStream in = new DataInputStream(System.in);
		
		DataInputStream cin = new DataInputStream(ssock.getInputStream());
		DataOutputStream cout = new DataOutputStream(ssock.getOutputStream());
		
		cout.writeUTF("RECEIVE");									//send command to server
		cout.writeUTF(filename);

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

		cout.writeUTF("LIST");
		System.out.println("Response from the server:");

		String data;

		
		while(true) {

			data = cin.readUTF();
			if(data.equals("EOF"))
				break;
			System.out.println(data);
		}
	}

	public void cd(Socket s) throws Exception {

		Socket ssock = s;

		DataInputStream in = new DataInputStream(System.in);
		DataInputStream cin = new DataInputStream(ssock.getInputStream());
		DataOutputStream cout = new DataOutputStream(ssock.getOutputStream());

		cout.writeUTF("CD");
		System.out.print("Enter path to directory: ");
		String path = in.readLine();
		cout.writeUTF(path);

		if(cin.readUTF().equals("OK"))
			System.out.println("Directory changed succefully");
	}

	public void pwd(Socket s) throws Exception {

		Socket ssock = s;

		DataInputStream cin = new DataInputStream(ssock.getInputStream());
		DataOutputStream cout = new DataOutputStream(ssock.getOutputStream());

		cout.writeUTF("PWD");
		System.out.println("Response from server:");
		System.out.println(cin.readUTF());
	}
}