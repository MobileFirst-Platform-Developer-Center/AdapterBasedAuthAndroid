package com.sample.adapterbasedauthandroid;

/**
* Copyright 2015 IBM Corp.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
import java.net.URI;
import java.net.URISyntaxException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.worklight.androidnativeadapterbasedauth.R;
import com.worklight.wlclient.api.WLClient;
import com.worklight.wlclient.api.WLResourceRequest;

public class MainAdapterBasedAuth extends Activity {

	private static TextView mainText = null;
	private Button invokeBtn, logoutBtn;	
	private static MainAdapterBasedAuth otherThis;
	
	private WLClient client;
	private AndroidChallengeHandler challengeHandler;	
	private String realm = "AuthRealm";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_adapter_based_auth);
		getActionBar().setTitle("Adapter Based Authentication");
		
		mainText = (TextView) findViewById(R.id.result);

		otherThis = this;
		
		//final WLClient client = WLClient.createInstance(this);
		client = WLClient.createInstance(this);
		challengeHandler = new AndroidChallengeHandler(this, realm);
		client.registerChallengeHandler(challengeHandler);
		client.connect(new MyConnectionListener());

		invokeBtn = (Button) findViewById(R.id.invoke);
		invokeBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				try {
					URI adapterPath = new URI("/adapters/AuthAdapter/getSecretData");
					WLResourceRequest request = new WLResourceRequest(adapterPath,WLResourceRequest.GET);
					request.send(new MyResponseListener());
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});
		
		logoutBtn = (Button) findViewById(R.id.logout);
		logoutBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				client.logout(realm, new MyRequestListener());			
			}
		});

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		boolean back = data.getBooleanExtra(LoginAdapterBasedAuth.Back, true);
		String username = data.getStringExtra(LoginAdapterBasedAuth.UserNameExtra);
		String password = data.getStringExtra(LoginAdapterBasedAuth.PasswordExtra);
		challengeHandler.submitLogin(resultCode, username, password, back);
	}

	public static void setMainText(final String txt){
		Runnable run = new Runnable() {			
			public void run() {
				mainText.setText(txt);				
			}
		};
		otherThis.runOnUiThread(run);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_adapter_based_auth, menu);
		return true;
	}

}
