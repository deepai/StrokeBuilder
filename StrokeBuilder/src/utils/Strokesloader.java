package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Strokesloader {
	
	public static HashMap<String,float[]> loadStrokes(String filename) throws Exception 
	{
		ObjectInputStream oin;
		oin=new ObjectInputStream(new FileInputStream(new File(filename)));
		HashMap<String,float[]> readObject = ((HashMap<String,float[]>) oin.readObject());	
		oin.close();
		return readObject;
	}
	
}
