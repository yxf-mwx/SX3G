package com.webapp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import android.util.Log;

public class ZipFactory {
	
//	public static void ZipFiles(String inputFile, String outputFile) throws IOException {
//		int pcount = outputFile.lastIndexOf("/");
//		String path = outputFile.substring(0, pcount);
//		File PFile = new File(path);
//		if (!PFile.exists()) {
//			PFile.mkdirs();
//		}
//
//		ZipOutputStream outputStream = new ZipOutputStream(
//				new FileOutputStream(outputFile));
//		File in = new File(inputFile);
//		if (!in.exists()) {
//			System.out.println("input File does not exits!");
//			return;
//		}
//
//		System.out.println("Zip Start..");
//		ZipFiles(outputStream, in, "");
//		System.out.println("Zip Done..");
//		outputStream.close();
//
//	}
//
//	private static void ZipFiles(ZipOutputStream out, File in, String base) throws IOException {
//		if (in.isDirectory()) {
//			File[] f = in.listFiles();
//			if (!"".equals(base)) {
//				out.putNextEntry(new ZipEntry(base + "/"));
//			}
//			base = base.length() == 0 ? "" : base + "/";
//			for (int i = 0; i < f.length; i++) {
//				ZipFiles(out, f[i], base + f[i].getName());
//			}
//		} else {
//			out.putNextEntry(new ZipEntry(base));
//			FileInputStream is = new FileInputStream(in);
//			int count = 0;
//			byte[] buffer = new byte[1024];
//			while ((count = is.read(buffer)) >= 0) {
//				out.write(buffer, 0, count);
//			}
//			is.close();
//		}
//	}

	public static void UnzipFiles(String inputFileName, String outputFileName) throws IOException {
		
		File outputFile=new File(outputFileName);
		if(!outputFile.exists()){
			outputFile.mkdirs();
		}
		
		File inputFile=new File(inputFileName);
		if(!inputFile.exists()){
			return ;
		}
		
		
		@SuppressWarnings("resource")
		ZipFile zip=new ZipFile(inputFileName);
		for(Enumeration entries=zip.entries();entries.hasMoreElements();) {
			ZipEntry entry=(ZipEntry)entries.nextElement();
			String zipEntryName=entry.getName();
			InputStream in=zip.getInputStream(entry);
			String outPath=outputFileName+zipEntryName;
			File file=new File(outPath.substring(0, outPath.lastIndexOf("/")));
			if(!file.exists()){
				file.mkdirs();
			}
			
			if(outPath.charAt(outPath.length()-1)=='/')
				outPath=outPath.substring(0,outPath.length()-1);
			
			if(new File(outPath).isDirectory()) {
				continue;
			}
			
			OutputStream os=new FileOutputStream(outPath);
			byte[] buffer=new byte[1024];
			int count=0;
			while((count=in.read(buffer))>=0){
				os.write(buffer, 0, count);
			}
			in.close();
			os.close();
		}
	}
	
}
