package fr.groupecraft.duel.commun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FilesUsing {
	private File file;
	
	public ArrayList<String> read() {
		ArrayList<String> r = new ArrayList<String>();
		if (!file.exists())return null;
		try (BufferedReader bw = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));) {
			String line= bw.readLine();
			while(line !=null) {
				r.add(line);
				line=bw.readLine();
			}
			
			bw.close();
			return r;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void write(List<String> content, Boolean CanCreate) {
		if(!file.exists() && CanCreate) {
				try {
					file.createNewFile();
					System.out.println("file created");
				} catch (IOException e) {
					e.printStackTrace();}
		} else if(!file.exists() && !CanCreate) {
			return;
		}
		if(file.exists()) {
			ArrayList<String> oldContent= read();
			ArrayList<String> toadd= new ArrayList<String>();
			toadd.addAll(oldContent);
			toadd.addAll(content);
			try {
				FileWriter writer= new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(writer);
				for(String o: toadd) {
					bw.write(o);
					bw.newLine();
				}
				bw.flush();
				bw.close();
				writer.close();
				return;
				} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			
		}
		return;
	}
	public void write(String content) {
		
		List<String> toadd= new ArrayList<String>();
		toadd.add(content);
		write(toadd, true);
	}
	public void writeWOK(ArrayList<String> content) {//write whith out keep
		System.out.println(file.getName());
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		try {
			FileWriter writer= new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(writer);
			for(String o: content) {
				bw.write(o);
				bw.newLine();
			}
			bw.flush();
			bw.close();
			writer.close();
			return;
			} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public  void rmvFirstInFile() {
		ArrayList<String> content= read();
		content.remove(0);
		writeWOK(content);
	}
	public void create() {
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public File getFile() {
		return this.file;
	}
	public FilesUsing(File file){
		this.file=file;
	}
	public void setFile(File file) {
		this.file=file;
	}
}
