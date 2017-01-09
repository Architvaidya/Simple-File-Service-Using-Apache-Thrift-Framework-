import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;


public class Client {
	public static int portNumber;
	public static String localHost;
	public static String operation;
	public static String fileName;
	public static String userName;
	
	private static void perform(FileStore.Client client){
		//System.out.println("Inside perform");
		FileReader fileReader;
		StringBuffer contentBuffer = null;
		BufferedReader reader;
		RFileMetadata fileMetaData;
		StatusReport status;
		RFile file = new RFile();
		//System.out.println("Operation is: "+operation);
		try{
			if(operation.equals("write")){
				File f = new File(fileName);
				//System.out.println("File name is: "+fileName);
				fileReader = new FileReader(f);
				reader = new BufferedReader(fileReader);
				contentBuffer = new StringBuffer();
				contentBuffer = contentBuffer.append("");
				String line;
				while((line = reader.readLine())!=null){
						
						contentBuffer.append(line);
						contentBuffer.append(System.getProperty("line.separator"));
					}
				fileMetaData = new RFileMetadata();
				File tempFile =  new File(fileName);
				fileMetaData.setFilename(tempFile.getName());
				fileMetaData.setOwner(userName);
				fileMetaData.setFilenameIsSet(true);
				fileMetaData.setOwnerIsSet(true);
				
				file = new RFile();
				String fileContents = contentBuffer.toString();
				file.setContent(fileContents);
				file.setContentIsSet(true);
				file.setMeta(fileMetaData);
				file.setMetaIsSet(true);
				status = client.writeFile(file);
				status.write(new TJSONProtocol.Factory().getProtocol(new TIOStreamTransport(System.out)));
				System.out.println();
			}
			
			if(operation.equals("list")){
				TProtocol tp = new TJSONProtocol.Factory().getProtocol(new TIOStreamTransport(System.out));
				try{
					List<RFileMetadata> list = client.listOwnedFiles(userName);
					tp.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, list.size()));
					for(RFileMetadata data:list){
						data.write(tp);
						System.out.println();
					}
					tp.writeListEnd();
					System.out.println();
				}catch(SystemException e){
					e.write(tp);
					System.out.println();
				}
				
				
			}
			if(operation.equals("read")){
				TProtocol tp = new TJSONProtocol.Factory().getProtocol(new TIOStreamTransport(System.out));
				RFile f = new RFile();
				try{
					f = client.readFile(fileName, userName);
					f.write(tp);
					System.out.println();
				}catch(SystemException e){
					e.write(tp);
					System.out.println();
				}
			}
			
		}catch(TException e){
			System.err.println(e.getMessage());
			System.exit(0);
		}catch(FileNotFoundException e){
			System.err.println("Unable to find file");
			System.exit(0);
		}catch(IOException e){
			System.err.println("Error in IO operation");
			System.exit(0);
		}
 	}

	public static void main(String[] args) {
		TTransport transport;
		
		if((args.length != 6) && (args.length != 8)){
			System.out.println("Number of parameters: "+args.length);
			System.out.println("Please enter valid number of parameters");
			System.exit(0);
		}
		if(args.length == 6){
			Map<String, String> argumentMapping = new HashMap<String,String>();
			localHost = args[0];
			try{
				portNumber = Integer.parseInt(args[1]);
			}catch(NumberFormatException e){
				System.err.println("port number must be a valid integer");
				System.exit(0);
			}
			
			for(int i = 2;i<args.length;i=i+2){
				argumentMapping.put(args[i], args[i+1]);
			}
			
			if(argumentMapping.containsKey("--operation") == false){
				System.err.println("No operation found");
				System.exit(0);
			}
			else{
				operation = argumentMapping.get("--operation");
				if((operation.equals("read") == true) || operation.equals("write") == true){
					System.err.println("Read or write not valid operation");
					System.exit(0);
				}
	
				
			}
			if(argumentMapping.containsKey("--user") == false){
				System.err.println("User name not specified");
				System.exit(0);
			}
			else{
				userName = argumentMapping.get("--user");
			}
			
			//System.out.println("Operation: "+operation);
			//System.out.println("user: "+userName);
			
			
		}
		else if(args.length == 8){
			Map<String, String> argumentMapping = new HashMap<String,String>();
			localHost = args[0];
			try{
				portNumber = Integer.parseInt(args[1]);
			}catch(NumberFormatException e){
				System.err.println("port number must be a valid integer");
				System.exit(0);
			}
			
			for(int i = 2;i<args.length;i=i+2){
				argumentMapping.put(args[i], args[i+1]);
			}
			//System.out.println(argumentMapping);
			if(argumentMapping.containsKey("--operation") == false){
				System.err.println("No operation found");
				System.exit(0);
			}
			else{
				operation = argumentMapping.get("--operation");
				if(operation.equals("list")){
					System.err.println("list not valid operation");
					System.exit(0);
				}
				if((operation.equals("read") == false) && operation.equals("write") == false){
				    System.err.println("Enter a valid operation");
				    System.exit(0);
				}
				
			}
			if(argumentMapping.containsKey("--filename") == false){
				System.err.println("File name not specified");
				System.exit(0);
			}
			else{
				fileName = argumentMapping.get("--filename");
			}
			
			if(argumentMapping.containsKey("--user") == false){
				System.err.println("User name not specified");
				System.exit(0);
			}
			else{
				userName = argumentMapping.get("--user");
			}

		}
		
		//System.out.println("user: "+userName);
		//System.out.println("File: "+fileName);
		//System.out.println("Operation: "+operation);
		
		
		try {
			transport = new TSocket(localHost, portNumber);
			transport.open();
			TProtocol protocol = new TBinaryProtocol(transport);
			FileStore.Client client = new FileStore.Client(protocol);
			perform(client);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		

	}

}
