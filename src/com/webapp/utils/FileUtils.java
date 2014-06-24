package com.webapp.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

public class FileUtils {
	//private String SDCardRoot;
	private String dataPath = "";

	public FileUtils(String dataPath) {
		//得到当前外部存储设备的目录
		//SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
		//当前应用存储路径/WebApp/
		this.dataPath = dataPath;
	}
	
	/**
	 * 在当前应用文件夹下创建文件
	 * 
	 * @throws IOException
	 */
	public File createFileInSDCard(String fileName) throws IOException {
		File file = new File(dataPath + File.separator + fileName);
		System.out.println("file---->" + file);
		file.createNewFile();
		return file;
	}
	
	/**
	 * 在当前应用文件夹下创建目录
	 * 
	 * @param dirName
	 */
	public File creatSDDir() {
		File dirFile = new File(dataPath + File.separator);
		System.out.println(dirFile.mkdirs());
		return dirFile;
	}

	/**
	 * 判断当前应用文件夹下的文件是否存在
	 */
	public boolean isFileExist(String fileName){
		File file = new File(dataPath + File.separator + fileName);
		return file.exists();
	}
	
	/**
	 * 将一个InputStream里面的数据写入到当前应用文件夹下
	 */
	public File write2SDFromInput(String fileName,InputStream input){
		
		File file = null;
		OutputStream output = null;
		try{
			creatSDDir();
			file = createFileInSDCard(fileName);
			output = new FileOutputStream(file);
			byte buffer [] = new byte[1024 * 1024];
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