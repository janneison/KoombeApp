package com.jannesion.koombeaapp;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

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
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Favorite extends Activity {
	
	private Almacenamiento almacenamiento;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite);
		almacenamiento=new Almacenamiento(getApplicationContext(),"KoombeaAPP",null,1);
		
		TareaWSFavorite tarea = new TareaWSFavorite();
		
		Vector<String> v=  almacenamiento.recuperarUsuario();
		String id_usuario = "";
        if(v!=null){
        	id_usuario = v.get(2);
        }
		tarea.execute(id_usuario);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.favorite, menu);
		return true;
	}
	
	//Tarea Asíncrona para llamar al WS de favoritos en segundo plano
			private class TareaWSFavorite extends AsyncTask<String,Integer,Boolean> {
				
				private ArrayList<Modelo> resultado;
	 
				
			    protected Boolean doInBackground(String... params) {
			    	
			    	boolean resul = true;
			    	
			    	HttpClient httpClient = new DefaultHttpClient();
					
			    	HttpPost post = new HttpPost("https://api.parse.com/1/functions/user_favorites");
					post.setHeader("content-type", "application/json");
					post.setHeader("X-Parse-Application-Id", getString(R.string.aplication_id));
					post.setHeader("X-Parse-REST-API-Key", getString(R.string.api_key));
					
					try
			        {			
						//Construimos el objeto cliente en formato JSON
						JSONObject dato = new JSONObject();
						
						//dato.put("Id", Integer.parseInt(txtId.getText().toString()));
						dato.put("user_id", params[0]);
						
						StringEntity entity = new StringEntity(dato.toString());
						post.setEntity(entity);
						
			        	HttpResponse resp = httpClient.execute(post);
			        	String respStr = EntityUtils.toString(resp.getEntity());
			        	
			        	JSONObject respJSON  = new JSONObject(respStr);
			        	
			        	JSONArray respJSONArr = respJSON.getJSONArray("result");
			        	
			        	ArrayList<Modelo> arraydir = new ArrayList<Modelo>();
			    		Modelo modelo;
			    		//Introduzco los datos			
			        	for(int i=0; i<respJSONArr.length(); i++)
			        	{
			        		JSONObject obj = respJSONArr.getJSONObject(i);
			        		
			        		String nombre = obj.getString("name");
			        		String idObjecto = obj.getString("objectId");
			        		JSONObject fechaJson = obj.getJSONObject("birthdate");
			        		String fecha = fechaJson.getString("iso");
			        		Date fechaReal = Utilidades.parseDateTime(fecha);
			        		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
			        		String fechaFormateada = df.format(fechaReal);
			        		long edad = Utilidades.calculateAge(fechaReal);
			        		JSONObject fotoJson = obj.getJSONObject("photo");
			        		String rutaFoto = fotoJson.getString("url");
			        		modelo = new Modelo(rutaFoto, nombre, fechaFormateada,edad,idObjecto);
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
			        	// Creo el adapter personalizado
			    		AdapterModelos adapter = new AdapterModelos(Favorite.this, this.resultado);
			        	ListView lista = (ListView) findViewById(R.id.lista_de_favoritos); 
			        	lista.setAdapter(adapter);
			        	lista.setOnItemClickListener(new OnItemClickListener() {
			                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
			                	
			                	String opcionSeleccionada = 
			                			((Modelo)a.getAdapter().getItem(position)).getIdObjecto();
			                	
			                	Intent i = new Intent(Favorite.this,DetailModel.class);
			                	i.putExtra("idObjecto", opcionSeleccionada);
			                	i.putExtra("favorito", "Favorite");
			                	
					    		startActivity(i);
			                }
			        	});
			    	}
			    }
			}


}
