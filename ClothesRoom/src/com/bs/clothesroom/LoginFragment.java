package com.bs.clothesroom;

import com.bs.clothesroom.provider.RoomProvider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class LoginFragment extends GeneralFragment implements OnClickListener {
	
	Button mRegister;
	Button mLogin;

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
		    ContentValues v = new ContentValues();
		    v.put("hehe", "heihei");
		    Uri uri = Uri.parse(RoomProvider.CONTENT_URI.toString()+"/users");
			getActivity().getContentResolver().insert(uri, v);
		}
	}

	
}
