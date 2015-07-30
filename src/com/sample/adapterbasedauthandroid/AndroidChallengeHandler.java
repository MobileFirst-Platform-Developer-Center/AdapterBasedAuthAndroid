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
package com.sample.adapterbasedauthandroid;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;

import com.sample.adapterbasedauthandroid.LoginAdapterBasedAuth;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLRequestOptions;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLProcedureInvocationData;
import com.worklight.wlclient.api.challengehandler.ChallengeHandler;

public class AndroidChallengeHandler extends ChallengeHandler{
	private Activity parentActivity;
	private WLResponse cachedResponse; 


	public AndroidChallengeHandler(Activity activity, String realm) {
		super(realm);
		parentActivity = activity;
	}

	@Override
	public void onFailure(WLFailResponse response) {
		submitFailure(response);
	}

	@Override
	public void onSuccess(WLResponse response) {
		submitSuccess(response);
	}

	@Override
	public boolean isCustomResponse(WLResponse response) {		
		try {
			if(response!= null && 
					response.getResponseJSON()!=null && 
					response.getResponseJSON().isNull("authRequired") != true && 
					response.getResponseJSON().getBoolean("authRequired") == true){
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void handleChallenge(WLResponse response){ 
		cachedResponse = response;
		Intent login = new Intent(parentActivity, LoginAdapterBasedAuth.class);
		parentActivity.startActivityForResult(login, 1);
	}
	
	
	public void submitLogin(int resultCode, String userName, String password, boolean back){
		if (resultCode != Activity.RESULT_OK || back) {
			submitFailure(cachedResponse);
		} else {
			Object[] parameters = new Object[]{userName, password};
			WLProcedureInvocationData invocationData = new WLProcedureInvocationData("AuthAdapter", "submitAuthentication");
			invocationData.setParameters(parameters);
			WLRequestOptions options = new WLRequestOptions();
			options.setTimeout(30000);
			submitAdapterAuthentication(invocationData, options);
		}
	}
}
