package com.jannesion.koombeaapp;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Models extends Activity {
	private Button btnFavoritos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_models);
		
		btnFavoritos=(Button)findViewById(R.id.modelo_favoritos);
		btnFavoritos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				formularioFavoritos();
			}
		});
		
		TareaWSModelos tarea = new TareaWSModelos();
		tarea.setModeloLy(this);
		tarea.execute("");
	}
	
	public void formularioFavoritos(){
		Intent i = new Intent(this,Favorite.class);
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.models, menu);
		return true;
	}
	
		//Tarea Asíncrona para llamar al WS de modelos en segundo plano
		private class TareaWSModelos extends AsyncTask<String,Integer,Boolean> {
			
			private ArrayList<Modelo> resultado;
			private Models modeloLy;
			
		    public Models getModeloLy() {
				return modeloLy;
			}

			public void setModeloLy(Models modeloLy) {
				this.modeloLy = modeloLy;
			}
 
			
		    protected Boolean doInBackground(String... params) {
		    	
		    	boolean resul = true;
		    	
		    	HttpClient httpClient = new DefaultHttpClient();
				
				HttpGet del = 
						new HttpGet("https://api.parse.com/1/classes/model");
				
				del.setHeader("content-type", "application/json");
				del.setHeader("X-Parse-Application-Id", getString(R.string.aplication_id));
				del.setHeader("X-Parse-REST-API-Key", getString(R.string.api_key));
				
				try
		        {			
		        	HttpResponse resp = httpClient.execute(del);
		        	String respStr = EntityUtils.toString(resp.getEntity());
		        	
		        	JSONObject respJSON  = new JSONObject(respStr);
		        	
		        	JSONArray respJSONArr = respJSON.getJSONArray("results");
		        	
		        	ArrayList<Modelo> arraydir = new ArrayList<Modelo>();
		    		Modelo modelo;
		    		// Introduzco los datos			
		        	for(int i=0; i<respJSONArr.length(); i++)
		        	{
		        		JSONObject obj = respJSONArr.getJSONObject(i);
		        		
		        		String idObjecto = obj.getString("objectId");
		        		String nombre = obj.getString("name");
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
		        	//Creo el adapter personalizado
		    		AdapterModelos adapter = new AdapterModelos(this.getModeloLy(), this.resultado);
		        	ListView lista = (ListView) findViewById(R.id.lista_de_modelos); 
		        	lista.setAdapter(adapter);
		        	lista.setOnItemClickListener(new OnItemClickListener() {
		                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
		                	      	
		                	String opcionSeleccionada = 
		                			((Modelo)a.getAdapter().getItem(position)).getIdObjecto();
		                	
		                	Intent i = new Intent(Models.this,DetailModel.class);
		                	i.putExtra("idObjecto", opcionSeleccionada);
		                	i.putExtra("favorito", "Unfavorite");
				    		startActivity(i);
		                }
		        	});
		    	}
		    }
		}
}
