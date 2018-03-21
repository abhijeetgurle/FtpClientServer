import java.io.*;
import java.net.*;
import java.util.regex.*;

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
 
			if(command_input[0].toUpperCase().equals("SEND")) {   
				//for uploading the file on server
				ftp.sendfile(s, command_input);
			}
			else if(command_input[0].toUpperCase().equals("RECEIVE")) {
				//for downloading the file on server
				ftp.receivefile(s, command_input);
			}
			else if(command_input[0].toUpperCase().equals("LIST")) {
				//For listing available files on server
				ftp.listdirectory(s);
			}
			else if(command_input[0].toUpperCase().equals("CD")) {
				//for changing directory
				ftp.cd(s, command_input);
			}
			else if(command_input[0].toUpperCase().equals("PWD")) {
				//for getting current working directory
				ftp.pwd(s);
			}
			else if(command_input[0].toUpperCase().equals("HELP")) {
				// Command for getting help
				ftp.help();
			}
			else if(command_input[0].toUpperCase().equals("CLOSE")) {
				// Command for getting help
				ftp.close(s);
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
	
	public void receivefile(Socket s, String[] command_input) throws Exception
	{
		Socket ssock=s;
		DataInputStream in=new DataInputStream(System.in);
		DataInputStream cin = new DataInputStream(ssock.getInputStream());
		DataOutputStream cout = new DataOutputStream(ssock.getOutputStream());
		
		cout.writeUTF("SEND");
		
		String filename = command_input[1];
		cout.writeUTF(filename);
		System.out.println("Receiving File " + filename);
		
		File f = new File(filename);
		int ch;
		String Response = cin.readUTF();
		
		if(Response.equals("NOT EXIST")) {				//if file does not exist

			System.out.println("File does not exists! Please Enter valid filename");
			return;
		}
		else {

			FileOutputStream fout = new FileOutputStream(f);
			ch=Integer.parseInt(Response);
			if(ch!=-1)
				fout.write(ch);
			do {

				ch=Integer.parseInt(cin.readUTF());
				if(ch!=-1)
					fout.write(ch);
			}while(ch!=-1);
			System.out.println("Received File...");
			fout.close();
		}
	}
	
	public void listdirectory(Socket s) throws Exception {
		
		Socket ssock = s;
		
		DataInputStream cin = new DataInputStream(ssock.getInputStream());
		DataOutputStream cout = new DataOutputStream(ssock.getOutputStream());

		cout.writeUTF("LIST");
		String data;
		
		while(true) {

			data = cin.readUTF();
			if(data.equals("EOF"))
				break;
			System.out.println(data);
		}
	}

	public void cd(Socket s, String[] command_input) throws Exception {

		Socket ssock = s;

		DataInputStream in = new DataInputStream(System.in);
		DataInputStream cin = new DataInputStream(ssock.getInputStream());
		DataOutputStream cout = new DataOutputStream(ssock.getOutputStream());

		cout.writeUTF("CD");
		String path = command_input[1];
		cout.writeUTF(path);

		if(cin.readUTF().equals("OK"))
			System.out.println("Directory changed succefully");
		else
			System.out.println("Directory Not Found! Please Enter Valid Path.");
	}

	public void pwd(Socket s) throws Exception {

		Socket ssock = s;

		DataInputStream cin = new DataInputStream(ssock.getInputStream());
		DataOutputStream cout = new DataOutputStream(ssock.getOutputStream());

		cout.writeUTF("PWD");
		System.out.println(cin.readUTF());
	}

	public void close(Socket s) throws Exception {

		Socket ssock = s;

		DataInputStream cin = new DataInputStream(ssock.getInputStream());
		DataOutputStream cout = new DataOutputStream(ssock.getOutputStream());

		try {
			
			cout.writeUTF("CLOSE");
			cin.close();
			cout.close();
			ssock.close();
			System.out.println("Good Bye!");
			System.exit(0);
		}
		catch  (SocketException ex) {
        	System.out.println("Good Bye!");
		}	
	}

	public void help() throws Exception {

		System.out.println("\n\n============Commands=============");
		System.out.println("\n\nsend filename -- To send file to server");
		System.out.println("\n\nreceive filename -- To receive file from server");
		System.out.println("\n\nlist -- To list all file available on current working directory");
		System.out.println("\n\ncd path -- To change directory to desire path");
		System.out.println("\n\npwd -- To know current working directory");
		System.out.println("\n\nclose -- To exit from the program");
		System.out.println("\n\nhelp -- To get help for commands");
	}
}