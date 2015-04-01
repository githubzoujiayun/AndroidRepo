package com.bs.clothesroom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends GeneralFragment implements OnClickListener {
	
	Button mRegister;
	Button mLogin;
	EditText mUser;
	EditText mPassw;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.login, container,false);
		mRegister = (Button) v.findViewById(R.id.register);
		mLogin = (Button) v.findViewById(R.id.login);
		mUser = (EditText) v.findViewById(R.id.username);
		mPassw = (EditText) v.findViewById(R.id.password);
		mRegister.setOnClickListener(this);
		mLogin.setOnClickListener(this);
		return v;
	}

	@Override
	public void onClick(View view) {
		long id = view.getId();
		if (id == R.id.register) {
			GeneralActivity.startRegister(getActivity());
			getActivity().finish(); 
		} else if (id == R.id.login) {
		    String username = mUser.getText().toString().trim();
		    String password = mPassw.getText().toString().trim();
		    mPrefs.saveUsername(username);
		    mPostController.login(username,password);
		}
	}

	
}
