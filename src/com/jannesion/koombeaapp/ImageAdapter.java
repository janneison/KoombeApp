package com.jannesion.koombeaapp;

import java.util.ArrayList;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
	protected Activity activity;
	protected ArrayList<Imagen> items;
	
	public ImageAdapter(Activity activity, ArrayList<Imagen> items) {
	    this.activity = activity;
	    this.items = items;
	  }

	@Override
	public int getCount() {
		return items.size();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// Generamos una convertView por motivos de eficiencia
		View v = convertView;
		
		//Asociamos el layout de la lista que hemos creado
		if(convertView == null){
			LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inf.inflate(R.layout.item_galeria, null);
		}
		
		// Creo un objeto de tipo imagen
		Imagen dir = items.get(position);
		//Incluyo la fotografia
		ImageView foto = (ImageView) v.findViewById(R.id.foto_galeria);
		UrlImageViewHelper.setUrlDrawable(foto, dir.ruta);
		//Incluyo el nombre
		TextView nombre = (TextView) v.findViewById(R.id.nombre_galeria);
		nombre.setText(dir.nombre);
		
		// Retornamos la vista
		return v;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return this.items.get(position);
	}


}
