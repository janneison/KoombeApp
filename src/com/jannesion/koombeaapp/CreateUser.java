package com.jannesion.koombeaapp;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CreateUser extends Activity implements android.view.View.OnClickListener{
	
	private TextView tvTitulo;
	private ProgressBar barraProgreso;
	private EditText etUsuario;
	private EditText etPasswd_1;
	private EditText etPasswd_2;
	private EditText etMail;
	private Button btnAceptar;
	private TextView tvLogin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_user);
		barraProgreso=(ProgressBar)findViewById(R.id.registro_progreso);
        tvTitulo=(TextView)findViewById(R.id.registro_titulo);
        etUsuario=(EditText)findViewById(R.id.registro_usuario);
        etPasswd_1=(EditText)findViewById(R.id.registro_passwd_1);
        etPasswd_2=(EditText)findViewById(R.id.registro_passwd_2);
        btnAceptar=(Button)findViewById(R.id.registro_btn_aceptar);
        btnAceptar.setTag("0");
        btnAceptar.setOnClickListener(this);
        tvLogin=(TextView)findViewById(R.id.ingreso_login);
        tvLogin.setTag("1");
        tvLogin.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_user, menu);
		return true;
	}
	
	public void formularioLogin(){
		Intent i = new Intent(this, Login.class);
		startActivityForResult(i, 1234);
	}
	
	public void registrarme(){
		String usuario=etUsuario.getText().toString();
		String pass_1=etPasswd_1.getText().toString();
		String pass_2=etPasswd_2.getText().toString();
		String msg="";
		//compruebo que los campos esten como se necesitan
		if(usuario.trim().equals("")){
			msg=getString(R.string.validate_user);
		}else if (pass_1.trim().equals("")){
			msg=getString(R.string.validate_password);
		}else if (pass_2.trim().equals("")){
			msg=getString(R.string.validate_repeat);
		}else if(!pass_1.equals(pass_2)){
			msg=getString(R.string.validate_password_register);
		}	
		
		if(msg.equals("")){
			TareaWSInsertar tarea = new TareaWSInsertar();
			tarea.setUserLy(this);
			tarea.execute(
					usuario,
					pass_1);
		}else{
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Koombea app")
				.setMessage(msg)
				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int id) {
								
							}
						});
			alert.show();
		}	
	}
	
	@Override
	public void onClick(View v) {
		int op = Integer.parseInt(v.getTag().toString());
		switch (op) {
		case 0:
			registrarme();
			break;
		case 1:
			formularioLogin();
			break;

		default:
			break;
		}
	}
	
		//Tarea Asíncrona para llamar al WS de inserción en segundo plano
		private class TareaWSInsertar extends AsyncTask<String,Integer,Boolean> {
			
			private CreateUser userLy;
			
		    public CreateUser getUserLy() {
				return userLy;
			}

			public void setUserLy(CreateUser userLy) {
				this.userLy = userLy;
			}

			protected Boolean doInBackground(String... params) {
		    	
		    	boolean resul = true;
		    	
		    	HttpClient httpClient = new DefaultHttpClient();
		        
				HttpPost post = new HttpPost("https://api.parse.com/1/users");
				post.setHeader("content-type", "application/json");
				post.setHeader("X-Parse-Application-Id", getString(R.string.aplication_id));
				post.setHeader("X-Parse-REST-API-Key", getString(R.string.api_key));
			
				try
		        {
					//Construimos el objeto cliente en formato JSON
					JSONObject dato = new JSONObject();
					
					//dato.put("Id", Integer.parseInt(txtId.getText().toString()));
					dato.put("username", params[0]);
					dato.put("password", params[1]);
					
					StringEntity entity = new StringEntity(dato.toString());
					post.setEntity(entity);
					
		        	HttpResponse resp = httpClient.execute(post);
		        	String respStr = EntityUtils.toString(resp.getEntity());
		        	JSONObject respJSON = new JSONObject(respStr);
		        	String objID = respJSON.getString("objectId");
		        	
		        	 Almacenamiento almacenamiento=new Almacenamiento(getApplicationContext(),"KoombeaAPP",null,1);
		        	 almacenamiento.recordarUsuario(params[1], params[2],objID);
		        	if(objID.length()==0)
		        		resul = false;
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
		    		Intent i = new Intent(this.getUserLy(),Models.class);
		    		startActivity(i);
		    	}
		    	else{
		    		
		    	}
		    }
		}

}
