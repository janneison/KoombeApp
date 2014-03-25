package com.jannesion.koombeaapp;

import java.text.DateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class DetailModel extends Activity {
	private String idObjecto; 
	private Button btnGaleria;
	private Button btnFavorito;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_model);
		btnFavorito=(Button)findViewById(R.id.detalle_favorito);
		btnGaleria=(Button)findViewById(R.id.detalle_album);
		btnGaleria.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				formularioGaleria();
			}
		});
		
		Bundle bundle = getIntent().getExtras();
		this.idObjecto = bundle.getString("idObjecto");
		String nombreFavorito = bundle.getString("favorito");
		btnFavorito.setText(nombreFavorito);
		TareaWSDetalleModelos tarea = new TareaWSDetalleModelos();
		tarea.execute(this.idObjecto);
	}
	
	public void formularioGaleria(){
		Intent i = new Intent(this,Galeria.class);
		i.putExtra("idObjecto", this.idObjecto);
		startActivity(i);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail_model, menu);
		return true;
	}
	
	//Tarea Asíncrona para llamar al WS el detalle del modelo en segundo plano
			private class TareaWSDetalleModelos extends AsyncTask<String,Integer,Boolean> {
				
				private String nombre;
				private String historia;
				private String fecha;
				private String edad;
				private String foto;
				
			    protected Boolean doInBackground(String... params){
			    	
			    	boolean resul = true;
			    	
			    	HttpClient httpClient = new DefaultHttpClient();
					
					HttpGet del = 
							new HttpGet("https://api.parse.com/1/classes/model/" + params[0]);
					
					del.setHeader("content-type", "application/json");
					del.setHeader("X-Parse-Application-Id", getString(R.string.aplication_id));
					del.setHeader("X-Parse-REST-API-Key", getString(R.string.api_key));
					
					try
			        {			
			        	HttpResponse resp = httpClient.execute(del);
			        	String respStr = EntityUtils.toString(resp.getEntity());
			        	
			        	JSONObject respJSON  = new JSONObject(respStr);
			        	this.nombre = respJSON.getString("name");
			        	this.historia = respJSON.getString("biography");
			        	JSONObject fechaJson = respJSON.getJSONObject("birthdate");
		        		String fecha = fechaJson.getString("iso");
		        		Date fechaReal = Utilidades.parseDateTime(fecha);
		        		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		        		String fechaFormateada = df.format(fechaReal);
		        		long edad = Utilidades.calculateAge(fechaReal);
		        		this.fecha = fechaFormateada;
		        		this.edad = "Age " + edad;
		        		JSONObject fotoJson = respJSON.getJSONObject("photo");
		        		this.foto = fotoJson.getString("url");
			        	
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
			    		TextView nombre = (TextView)findViewById(R.id.detalle_nombre);
			    		nombre.setText(this.nombre);
			    		
			    		TextView historia = (TextView)findViewById(R.id.detalle_historia);
			    		historia.setText(this.historia);
			    		
			    		TextView fecha = (TextView)findViewById(R.id.detalle_nacimiento);
			    		fecha.setText(this.fecha);
			    		
			    		TextView edad = (TextView)findViewById(R.id.detalle_edad);
			    		edad.setText(this.edad);
			    		
			    		ImageView foto = (ImageView) findViewById(R.id.foto_detalle);
			    		UrlImageViewHelper.setUrlDrawable(foto, this.foto);
			    
			    	}
			    }
			}


}
