package com.example.strokebuilder;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import utils.*;
import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class StrokeBuilderActivity extends Activity {
	
	ListView StrokeClassDisplay;
	ImageView bg;
	GestureOverlayView drawingsurface;
	Button newClass;
	Button Save;
	Button Exit;
	EditText Classname;
	ArrayList<String> list=new ArrayList<String>();
	ArrayAdapter<String> listconnector;
	String menuItem="delete";
	String filename="/mnt/sdcard/Library.dat";
	TextView listCount;
	HashMap<String,float[]> library;;  //load the library
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
        	library=Strokesloader.loadStrokes(filename);
        }catch (Exception e)
        {
        	
        }
        
        listCount=(TextView) findViewById(R.id.textView1);
        listCount.setTextColor(Color.YELLOW);
        StrokeClassDisplay=(ListView) findViewById(R.id.listView1); //display the names here
        listconnector=new ArrayAdapter<String>(getApplicationContext(),R.layout.listview,list);
        StrokeClassDisplay.setAdapter(listconnector);
        
        if(library==null)
        	library=new HashMap<String,float[]>();
        else
        {
        	list.clear();
        	list.addAll(library.keySet());
        	listconnector.notifyDataSetChanged();
        	listCount.setText("Count = "+list.size());
        }
        		
        
        drawingsurface=(GestureOverlayView) findViewById(R.id.gestureOverlayView1); //drawing surface
        drawingsurface.setGestureColor(Color.CYAN);
        newClass=(Button) findViewById(R.id.button1); //Button to draw a new interface
        newClass.setTextColor(Color.WHITE);
       Save=(Button) findViewById(R.id.button2); //save and update 
        Exit=(Button) findViewById(R.id.button3); //exit button 
        Classname=(EditText) findViewById(R.id.editText1); //enter the name of the class here
        bg=(ImageView) findViewById(R.id.imageView1);
       
        Exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				System.exit(0);
			}
		});
        Exit.setTextColor(Color.WHITE);
       /*
        * Clear the Drawing background 
        */
        newClass.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				bg.setImageBitmap(null);
			}
		});
        /*
         * Save the Object File
         */
        Save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int i=SaveFile.WriteFile(filename,library);
				if(i==0)
					Toast.makeText(getApplicationContext(), "Library file Saved Successfully!You may now quit the application.",Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(getApplicationContext(), "Error in Saving file!Try again later",Toast.LENGTH_LONG).show();
				
			}
		});
        
        Save.setTextColor(Color.WHITE);
        /*
         *  Add the gesture Listener interface on the drawingSurface to draw the gesture and recieve the float array
         */
        drawingsurface.addOnGestureListener(new OnGestureListener() {
			
			@Override
			public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
				// TODO Auto-generated method stub
				Gesture g=overlay.getGesture();
				String name=Classname.getText().toString();
				
				if(name.equals(""))
				{
					Toast.makeText(getApplicationContext(),"Empty String  !Enter a Class Name",Toast.LENGTH_LONG).show();
				}
				else if(library.containsKey(name))
					Toast.makeText(getApplicationContext(), "Class "+name+" already exists.",Toast.LENGTH_LONG).show();
				else
				{
					float[] temp=preprocessing.Scaling.scale(g.getStrokes().get(0).points);
					temp=preprocessing.smoothing.smoothFunction(temp);
					library.put(name,temp);
					Toast.makeText(getApplicationContext(), "class "+name+" successfully added to the library",Toast.LENGTH_SHORT).show();
					list.clear();
					list.addAll(library.keySet());
					listconnector.notifyDataSetChanged();
					
					listCount.setText("Count = "+list.size());
										
					
				}
				
			}
			
			@Override
			public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGesture(GestureOverlayView overlay, MotionEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
        
        /*
         * Connect the listconnector adapter to the ListView(StrokeClassDisplay)
         */
         registerForContextMenu(StrokeClassDisplay); //register the listview for the Context Menu
         
         /*
          * Set StrokeClassDisplay ListView to draw the image onClicking it.
         */
         StrokeClassDisplay.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				String temp=list.get(arg2);
				float[] Stroke=library.get(temp);
				Paint pt=new Paint();
				pt.setColor(Color.RED);
				pt.setStrokeWidth(3);
			/*
			 * Set the image as the background 	
			 */
				
				Bitmap bp=Bitmap.createBitmap(400,400,Bitmap.Config.ARGB_8888);
				Canvas ct=new Canvas(bp);
				ct.drawPoints(Stroke, pt);
				bg.setImageBitmap(bp);
				
				
			}
		});
   
    }
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
      if (v.getId()==R.id.listView1) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle(list.get(info.position));
        menu.add(Menu.NONE,0,0, menuItem);      
      }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
      AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
      int menuItemIndex = info.position;
     // String menu=menuItem;
      String listItemName = list.get(menuItemIndex);
      list.remove(menuItemIndex);
      library.remove(listItemName);
      listconnector.notifyDataSetChanged();
      listCount.setText("Count = "+list.size());
      Toast.makeText(getApplicationContext(), "deleted class "+listItemName,Toast.LENGTH_SHORT).show();
      return true;
    }

   
 }
