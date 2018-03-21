import java.io.*;
import java.net.*;
import java.util.regex.*;

class server implements Runnable {
	
	Socket s;
	server(Socket s) {

		this.s = s;
	}

	public static void main(String[] args) throws Exception {
		
		ServerSocket Sock = new ServerSocket(Integer.parseInt(args[0]));
		System.out.println("Listening for requests.");

		while(true) {

			Socket s = Sock.accept();
			System.out.println("Connected");
			new Thread(new server(s)).start();
		}
	}

	public void sendfile(Socket s) throws Exception {
		
		Socket ssock = s;

		DataInputStream cin = new DataInputStream(ssock.getInputStream());
		DataOutputStream cout = new DataOutputStream(ssock.getOutputStream());

		String filename = cin.readUTF();
		System.out.println("Reading file "+filename);
		File f = new File(System.getProperty("user.dir") + File.separator +filename);
		System.out.println(f);
		boolean exists = f.exists();
		if(exists == false) {				//if file does not exists return

			cout.writeUTF("NOT EXIST");
			return;
		}
		

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

		File folder = new File(System.getProperty("user.dir") + ".");
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
		if(path.equals("..")) {

			String currentdir = System.getProperty("user.dir");
			String pattern = Pattern.quote(System.getProperty("file.separator"));
			String[] split_path = currentdir.split(pattern);
			String parentPath = "";
			for(int i=0;i<split_path.length-1;i++)
				parentPath+=split_path[i] + System.getProperty("file.separator");

			System.setProperty("user.dir", parentPath);
		}
		else if(path.charAt(0) == 'C') {

			File file=new File(path + ".");
			boolean exists = file.exists();
			if(exists == false) {				//if folder does not exists return

				cout.writeUTF("NOT EXIST");
				return;
			}
      		System.setProperty("user.dir", path);

		}
		else {

			File file=new File(".");
			String oldpath = file.getAbsolutePath();
      		oldpath = oldpath.substring(0, oldpath.length() - 1);
      		File folder = new File(oldpath+path);
			boolean exists = folder.exists();
			if(exists == false) {				//if folder does not exists return

				cout.writeUTF("NOT EXIST");
				return;
			}
      		System.setProperty("user.dir", oldpath+path);
		}
      	cout.writeUTF("OK");
	}

	public void pwd(Socket s) throws Exception {

		Socket ssock = s;
		DataInputStream cin = new DataInputStream(ssock.getInputStream());
		DataOutputStream cout = new DataOutputStream(ssock.getOutputStream());

		File file=new File(".");
      	String oldpath = file.getAbsolutePath();
      	oldpath = oldpath.substring(0, oldpath.length() - 1);

      	cout.writeUTF(oldpath);
	}

	public void close(Socket s) throws Exception {

		Socket ssock = s;
		DataInputStream cin = new DataInputStream(ssock.getInputStream());
		DataOutputStream cout = new DataOutputStream(ssock.getOutputStream());

		try {
        	cout.close();
        	cin.close();
        	ssock.close();
    	} catch (IOException ex) {
        	System.out.println("Error closing the socket and streams");
		}
}			

	public void run() {

		try {

			DataInputStream cin = new DataInputStream(s.getInputStream());
			DataOutputStream cout = new DataOutputStream(s.getOutputStream());

			server ftp = new server(s);
		
			while(true)
			{
				String option = cin.readUTF();
			
				if(option.equals("SEND")) {
				
					System.out.println("SEND Command Received");
					ftp.sendfile(ftp.s);
				}
				else if(option.equals("RECEIVE")) {
				
					System.out.println("RECEIVE Command Received");
					ftp.receivefile(ftp.s);
				}
				else if(option.equals("LIST")) {

					System.out.println("LIST Command Received");
					ftp.listdirectory(ftp.s);
				}
				else if(option.equals("CD")) {

					System.out.println("CD Command Received");
					ftp.cd(ftp.s);
				}
				else if(option.equals("PWD")) {

					System.out.println("PWD Command Received");
					ftp.pwd(ftp.s);
				}
				else if(option.equals("CLOSE")) {

					System.out.println("CLOSE Command Received");
					ftp.close(ftp.s);
				}
			}
		} catch (Exception e) {

			System.out.println(e);
		}
	}	
	
}