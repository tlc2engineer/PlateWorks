package com.plateworks;

import com.example.plateworks.R;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Фрагмент авторизации.
 */
public class LoginFragment extends Fragment {

	private EditText login;
	private EditText pass;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Получение view
		View v = inflater.inflate(R.layout.front_view, container,false);
		// Идентификация элементов
		Button btn = (Button) v.findViewById(R.id.commit_btn);
		login=(EditText) v.findViewById(R.id.log_field);
		pass=(EditText) v.findViewById(R.id.pass_field);
		// Установка обработчика кнопки входа
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				LogInOut linOut=(LogInOut) getActivity();
			 String loginTxt = login.getText().toString();
			 String passTxt = pass.getText().toString();
			if(loginTxt.equals("amk")&& passTxt.equals("amk"))
				linOut.logOn();
			else{
				Builder badLogin = new AlertDialog.Builder(getActivity());
				badLogin.setMessage("Неправильный логин").setTitle("Сообщение");
				badLogin.create().show();
			}
			}
			
		});

		return v;
	}

}
