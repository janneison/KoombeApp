package com.jannesion.koombeaapp;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;


public class Galeria extends Activity {
	private String idObjecto; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_galeria);
		Bundle bundle = getIntent().getExtras();
		this.idObjecto = bundle.getString("idObjecto");
		TareaWSGaleria tarea = new TareaWSGaleria();
		tarea.execute(this.idObjecto);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.galeria, menu);
		return true;
	}
	
	private class TareaWSGaleria extends AsyncTask<String,Integer,Boolean> {
		
		private ArrayList<Imagen> resultado;

		
	    protected Boolean doInBackground(String... params) {
	    	
	    	boolean resul = true;
	    	
	    	HttpClient httpClient = new DefaultHttpClient();
			
	    	HttpPost post = new HttpPost("https://api.parse.com/1/functions/model_photos");
			post.setHeader("content-type", "application/json");
			post.setHeader("X-Parse-Application-Id", getString(R.string.aplication_id));
			post.setHeader("X-Parse-REST-API-Key", getString(R.string.api_key));
			
			try
	        {			
				//Construimos el objeto cliente en formato JSON
				JSONObject dato = new JSONObject();
				
				//dato.put("Id", Integer.parseInt(txtId.getText().toString()));
				dato.put("model_id", params[0]);
				
				StringEntity entity = new StringEntity(dato.toString());
				post.setEntity(entity);
				
	        	HttpResponse resp = httpClient.execute(post);
	        	String respStr = EntityUtils.toString(resp.getEntity());
	        	
	        	JSONObject respJSON  = new JSONObject(respStr);
	        	
	        	JSONArray respJSONArr = respJSON.getJSONArray("result");
	        	
	        	ArrayList<Imagen> arraydir = new ArrayList<Imagen>();
	        	Imagen modelo;
	    		//Introduzco los datos			
	        	for(int i=0; i<respJSONArr.length(); i++)
	        	{
	        		JSONObject obj = respJSONArr.getJSONObject(i);
	        		
	        		String nombre = "Image" + (i+1);
	        		JSONObject fotoJson = obj.getJSONObject("photo");
	        		String rutaFoto = fotoJson.getString("url");
	        		modelo = new Imagen();
	        		modelo.nombre = nombre;
	        		modelo.ruta = rutaFoto;
		    		arraydir.add(modelo);
	        	}
	        	resultado = arraydir;
	        }
	        catch(Exception ex)
	        {
	        	Log.e("ServicioRest","Error!", ex);
	        	resul = false;
	        }
	 
	        return resul;
	    }
	    
	    protected void onPostExecute(Boolean result) {
	    	
	    	if (result)
	    	{
	        	//Creo el adapter personalizado
	    		ImageAdapter adapter = new ImageAdapter(Galeria.this, this.resultado);
	        	ListView lista = (ListView) findViewById(R.id.lista_de_imagenes); 
	        	lista.setAdapter(adapter);
	        	
	    	}
	    }
	}


}
