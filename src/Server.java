
import java.io.File;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class Server {
	
	public static FileStore.Processor<FileStore.Iface> processor;
	public static Handler handler;
	static int portNo = 0;

	public static void main(String[] args) {
		TServerTransport serverTransport;
		TServer server = null;
		File root = new File("Root");
		
		if(root.exists()){
			clearRoot(root);
		}
		root.mkdir();
		
		
		if(args.length == 0){
			System.out.println("Please enter port number...");
			System.exit(0);
		}
		Server.portNo = Integer.parseInt(args[0]);
		
		try{
			handler = new Handler();
			processor = new FileStore.Processor<FileStore.Iface>(handler);
			serverTransport = new TServerSocket(Server.portNo);
			server = new TSimpleServer(new Args(serverTransport).processor(processor));
			System.out.println("Starting the server on port number "+Server.portNo+"...");
			server.serve();
			
		}catch(Exception e){
			
		}
		
	}
	
	public static void clearRoot(File root){
		
			if(root.isDirectory()){
				if(root.list().length == 0){
					root.delete();
				}
				else{
					String[] files = root.list();
					
					for(String fileName:files){
						File fileToDelete = new File(root,fileName);
						clearRoot(fileToDelete);
					}
					if(root.list().length == 0){
						root.delete();
					}
				}
			}
			else{
				root.delete();
			}
		
		
		
	}

}
