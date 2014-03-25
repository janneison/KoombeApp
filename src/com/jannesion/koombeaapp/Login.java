package com.jannesion.koombeaapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Login extends Activity implements android.view.View.OnClickListener {

	private EditText etUsuario;
	private EditText etPasswd;	
	private Button btnAceptar;
	private TextView tvRegistrarme;
	private CheckBox cbRecordarAcceso;
	private String usuario;
	private Almacenamiento almacenamiento;
	private String password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
        etUsuario=(EditText)findViewById(R.id.ingreso_usuario);
        etPasswd=(EditText)findViewById(R.id.ingreso_passwd);
        cbRecordarAcceso=(CheckBox)findViewById(R.id.ingreso_recordarme);
        btnAceptar=(Button)findViewById(R.id.ingreso_aceptar);
        btnAceptar.setTag("0");
        btnAceptar.setOnClickListener(this); 
        tvRegistrarme=(TextView)findViewById(R.id.ingreso_registrar);
        tvRegistrarme.setTag("1");
        tvRegistrarme.setOnClickListener(this);
        
        almacenamiento=new Almacenamiento(getApplicationContext(),"KoombeaAPP",null,1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	public void iniciarSesion(){
		usuario=etUsuario.getText().toString();
		password=etPasswd.getText().toString();
		boolean recordar=cbRecordarAcceso.isChecked();
		String msg="";
		if(usuario.trim().equals("")){
			msg=getString(R.string.validate_user);
		}else if (password.trim().equals("")){
			msg=getString(R.string.validate_password);
		}
		if(msg.equals("")){
			TareaWSLogin tarea = new TareaWSLogin();
			String params = "username=" + usuario + "&password=" + password;
			tarea.setLoginLay(this);
			tarea.setAlmacenamiento(this.almacenamiento);
			tarea.execute(params,usuario,password);			
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
	
	public void formularioRegistrar(){
		Intent i = new Intent(this,CreateUser.class);
		startActivity(i);
	}
		
	@Override
	public void onClick(View v) {
		int op = Integer.parseInt(v.getTag().toString());
		switch (op) {
		case 0:
			iniciarSesion();
			break;
		case 1:
			formularioRegistrar();
			break;
		default:
			break;
		}
	}
	
		//Tarea Asíncrona para llamar al servicio y loguearme
		private class TareaWSLogin extends AsyncTask<String,Integer,Boolean> {
			
			private Login loginLay;
			private Almacenamiento almacenamiento;
			
			public Login getLoginLay() {
				return loginLay;
			}

			public void setLoginLay(Login value) {
				this.loginLay = value;
			}
			
			public Almacenamiento getAlmacenamiento() {
				return almacenamiento;
			}

			public void setAlmacenamiento(Almacenamiento value) {
				this.almacenamiento = value;
			}
			
		    protected Boolean doInBackground(String... params) {
		    	
		    	boolean resul = true;
		    	
		    	HttpClient httpClient = new DefaultHttpClient();
		        
				String id = params[0];
				
				HttpGet del = 
						new HttpGet("https://api.parse.com/1/login?" + id);
				
				del.setHeader("content-type", "application/json");
				del.setHeader("X-Parse-Application-Id", getString(R.string.aplication_id));
				del.setHeader("X-Parse-REST-API-Key", getString(R.string.api_key));
				try
		        {			
		        	HttpResponse resp = httpClient.execute(del);
		        	String respStr = EntityUtils.toString(resp.getEntity());
		        	JSONObject respJSON = new JSONObject(respStr);
		        	String sessionToken = respJSON.getString("sessionToken");
		        	String idUsuario = respJSON.getString("objectId");
		        	this.getAlmacenamiento().recordarUsuario(params[1], params[2],idUsuario);
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
		    		Intent i = new Intent(this.getLoginLay(),Models.class);
		    		startActivity(i);
		    	}else{
		    		AlertDialog.Builder alert = new AlertDialog.Builder(this.getLoginLay());
					alert.setTitle("Koombea app")
						.setMessage(R.string.validate_incorrect)
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

		}
}
