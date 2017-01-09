import java.io.File;
import java.io.FileWriter;

import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.slf4j.Logger;

public class Handler implements FileStore.Iface{
	String folderName;
	List<RFile> rFileList = new ArrayList<RFile>();
	Map<String, ArrayList<RFile>> ownerData = new HashMap<String, ArrayList<RFile>>(); 
	

	@Override
	public List<RFileMetadata> listOwnedFiles(String user) throws SystemException, TException {
		
		if(ownerData.containsKey(user) == false){
			SystemException systemException = new SystemException();
			systemException.setMessage("The owner does not exist");
			throw systemException;
		}
		ArrayList<RFile> tempRfileList = ownerData.get(user);
		List<RFileMetadata>RFileList = new ArrayList<RFileMetadata>();
		if(tempRfileList.isEmpty() == true){
			SystemException systemException = new SystemException();
			systemException.setMessage("The owner "+user+"does not own any file");
			throw systemException;
		}
		for(RFile file:tempRfileList){
			RFileList.add(file.getMeta());
		}
		return RFileList;
	}

	@Override
	public StatusReport writeFile(RFile rFile) throws SystemException, TException {
		StringBuffer buffer;
		
		folderName = rFile.getMeta().getOwner();
		
		//No rFile.getMeta.getOwner is not present in ownerdata
		//Make new owner directory
		if(ownerData.containsKey(folderName) == false){
			File directoryName = new File("Root"+File.separator+folderName);
			directoryName.mkdir();
			Date date = new Date();
			Timestamp timeStamp = new Timestamp(date.getTime());
			File file = new File("Root"+File.separator+folderName+File.separator+rFile.getMeta().getFilename());
			ArrayList<RFile> tempList = new ArrayList<RFile>();
			tempList.add(rFile);
			ownerData.put(rFile.getMeta().getOwner(), tempList);
			StatusReport statusReport = fileBuilder(file,rFile,folderName, date.getTime());
			return statusReport;
		}
		File file = new File("Root"+File.separator+folderName+File.separator+rFile.getMeta().getFilename());
		try{
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] mdHash = messageDigest.digest(rFile.getContent().getBytes());
			buffer = new StringBuffer();
			for(int i = 0; i<mdHash.length;i++){
				//buffer.append(String.format("%02x", i&0xff));
				buffer.append(Integer.toString((mdHash[i]&0xff) + 0x100, 16).substring(1));
			}
			String messageDigestString = buffer.toString();
			
			if(file.exists()){
				FileWriter fileWriter = new FileWriter(file);
				fileWriter.write(rFile.getContent());
				fileWriter.close();
				ArrayList<RFile> tempList = ownerData.get(rFile.getMeta().getOwner());
				String rFileName = rFile.getMeta().getFilename();
				RFile fileInTempList; 
				for(int i = 0; i<tempList.size();i++){
					if(tempList.get(i).getMeta().getFilename().equals(rFileName)){
						tempList.get(i).getMeta().setUpdated(file.lastModified());
						tempList.get(i).getMeta().setUpdatedIsSet(true);
						tempList.get(i).getMeta().setVersion(tempList.get(i).getMeta().getVersion()+1);
						tempList.get(i).getMeta().setContentLength(rFile.getContent().length());
						tempList.get(i).getMeta().setContentHash(messageDigestString);
						tempList.get(i).setContent(rFile.getContent());
					}
				}
				
			}
			
			//File does not exist
			 
			else{
				ArrayList<RFile> tempList = new ArrayList<RFile>();
				//If the owners data is empty
				if(ownerData.get(rFile.getMeta().getOwner()) == null){
					tempList.add(rFile);
					ownerData.put(rFile.getMeta().getOwner(), tempList);
				}
				//If there are already some files in owner data 
				else{
					ownerData.get(rFile.getMeta().getOwner()).add(rFile);
				}
				
				file.createNewFile();
				FileWriter fileWriter = new FileWriter(file);
				fileWriter.write(rFile.getContent());
				fileWriter.close();
				//String fileContents = rFile.getContent().replace("\r", " ");
				//rFile.setContent(fileContents);
				Date date = new Date();
				rFile.getMeta().setCreated(file.lastModified());
				rFile.getMeta().setUpdated(file.lastModified());
				rFile.getMeta().setVersion(0);
				rFile.getMeta().setContentLength(rFile.getContent().length());
				rFile.getMeta().setContentHash(messageDigestString);
				
				rFile.getMeta().setContentHashIsSet(true);
				rFile.getMeta().setUpdatedIsSet(true);
				rFile.getMeta().setContentHashIsSet(true);
				rFile.getMeta().setVersionIsSet(true);
				rFile.getMeta().setContentLengthIsSet(true);
				rFile.getMeta().setContentHashIsSet(true);
				
			}
		}catch(Exception e){
			System.out.println("Exception at line 111");
			e.printStackTrace();
			StatusReport statusReport = new StatusReport(Status.FAILED);
			return statusReport;
		}
		StatusReport statusReport = new StatusReport(Status.SUCCESSFUL);
		return statusReport;
	}
	
	public StatusReport fileBuilder(File file, RFile rFile, String folderName2, long l ){
		//System.out.println("Inside fileBuilder");
		
		try{
			StringBuffer buffer = new StringBuffer();
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] mdHash = messageDigest.digest(rFile.getContent().getBytes());
			buffer = new StringBuffer();
			for(int i = 0; i<mdHash.length;i++){
				//buffer.append(String.format("%02x", i&0xff));
				buffer.append(Integer.toString((mdHash[i]&0xff) + 0x100, 16).substring(1));
			}
			String messageDigestString = buffer.toString();
			//file = new File("Root"+File.separator+folderName+File.separator+rFile.getMeta().getFilename());
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(rFile.getContent());
			fileWriter.close();
			Date date = new Date();
			rFile.getMeta().setCreated(file.lastModified());
			rFile.getMeta().setUpdated(file.lastModified());
			rFile.getMeta().setVersion(0);
			rFile.getMeta().setContentLength(rFile.getContent().length());
			rFile.getMeta().setContentHash(messageDigestString);
			
			rFile.getMeta().setContentHashIsSet(true);
			rFile.getMeta().setUpdatedIsSet(true);
			rFile.getMeta().setContentHashIsSet(true);
			rFile.getMeta().setVersionIsSet(true);
			rFile.getMeta().setContentLengthIsSet(true);
			rFile.getMeta().setContentHashIsSet(true);

						
		}catch(Exception e){
			e.printStackTrace();
			StatusReport statusReport = new StatusReport(Status.FAILED);
			return statusReport;
		}
		
		StatusReport statusReport = new StatusReport(Status.SUCCESSFUL);
		return statusReport;
	}

	@Override
	public RFile readFile(String filename, String owner) throws SystemException, TException {
		if(ownerData.containsKey(owner) == false){
			SystemException systemException = new SystemException();
			systemException.setMessage("The owner does not exist");
			throw systemException;
		}
		ArrayList<RFile> tempRfileList = ownerData.get(owner);
		
		if(tempRfileList.isEmpty() == true || tempRfileList.size() == 0||tempRfileList == null){
			SystemException systemException = new SystemException();
			systemException.setMessage("The owner "+owner+"does not own any file");
			throw systemException;
		}
		
		
		boolean flag = false;
		for(RFile rFile: tempRfileList){
			if(rFile.getMeta().getFilename().equals(filename)){
				flag = true;
				return rFile;
			}
		}
		if(flag == false){
			SystemException systemException = new SystemException();
			systemException.setMessage("The owner "+owner+" does not own the file "+filename);
			throw systemException;
		}
		
		return null;
	}

}
