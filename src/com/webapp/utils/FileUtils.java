package com.webapp.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
import android.util.Log;

public class FileUtils {
	//private String SDCardRoot;
	private String dataPath = "";

	public FileUtils() {
		//the basepath is 
		this.dataPath = Environment.getDataDirectory().getPath()+File.separator+"data/shixun.gapmarket/download";
		File file=new File(dataPath);
		if(!file.exists()){
			Log.d("yxf_download_filenot exits", dataPath);
			file.mkdirs();
		}
		file=null;
	}
	
	/**
	 * Create a new file in the download package
	 * @param the download package file name
	 * @throws IOException
	 */
	public File createFileInSDCard(String fileName) throws IOException {
		File file = new File(dataPath +fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * judge weather the file already exits
	 * @param fileName the output file Name
	 * @return boolean true if its exits
	 */
	public boolean isFileExist(String fileName){
		File file = new File(dataPath + File.separator + fileName);
		return file.exists();
	}
	
	/**
	 * This function is used for write a stream to a file in SDcard
	 * @param fileName is the  output file name
	 * @param input is the input stream
	 * @return the output file
	 */
	public File write2SDFromInput(String fileName,InputStream input){
		
		File file = null;
		OutputStream output = null;
		try{
			output = new FileOutputStream(dataPath+fileName);
			byte buffer [] = new byte[32 * 1024];
			int temp ;
			while((temp = input.read(buffer)) != -1){
				output.write(buffer, 0, temp);
			}
			output.flush();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				output.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return file;
	}

}