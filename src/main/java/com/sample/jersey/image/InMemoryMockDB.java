package com.sample.jersey.image;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryMockDB {
	
	private static int imageIdMockSequence = 0;
	
	private static Map<Integer, ImageData> data = new HashMap<Integer,ImageData>();
	
	
	public static Collection<ImageData> list(){
		return data.values();
	}

	public static ImageData addImage(String title){
		ImageData imageData = new ImageData();
		
		imageData.id = ++imageIdMockSequence;
		imageData.setCreatedDate(new Date());
		imageData.setTitle(title);
		
		data.put(imageData.id, imageData);
		
		return imageData;
	}

	public static ImageData getImage(Integer id){
		return data.get(id);
	}
}
