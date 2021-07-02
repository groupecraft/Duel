package fr.groupecraft.duel.commun;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.bukkit.inventory.Inventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class FileGSON<T> {
	private FilesUsing fu;
	private Gson gson;
	
	public FileGSON(File file) {
		fu=new FilesUsing(file);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private Gson createGsonInstanceArenas() {
		return new GsonBuilder()
				.setPrettyPrinting()
				.serializeNulls()
				.disableHtmlEscaping()
//				.enableComplexMapKeySerialization()
//				.excludeFieldsWithoutExposeAnnotation()
//				.registerTypeAdapter(T.class, new MyTypeAdapter<T>())
				.create();//crée le gson
	}
	public void serialize(T obj) {
		gson = createGsonInstanceArenas();
		if(!fu.getFile().exists()) {
			fu.create();
		}
		String objJson = gson.toJson(obj); //transforme l'ArrayList en notation json
		System.out.println(objJson);
		ArrayList<String> a=new ArrayList<String>();
		a.add(objJson);
		fu.writeWOK(a);//écrit la notation json dans le ficher
	}
	public T deSerialize(TypeToken <T>typeToken) {
		gson= createGsonInstanceArenas();
		if(!fu.getFile().exists()) {
			fu.create();
		}
		String json="";
		ArrayList<String> content = fu.read();
		for(int i=0; i<content.size(); i++) {
			json+=content.get(i);
		}//transforme l'arrayList en string
		T var;
		var=gson.fromJson(json,typeToken.getType());
		return var;
	}
	public File getFile() {
		return fu.getFile();
	}
}
