/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package org.aerogear.otp.android.demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.jboss.aerogear.android.http.HeaderAndBody;
import org.jboss.aerogear.android.pipeline.AbstractActivityCallback;

import java.util.List;
import org.jboss.aerogear.android.pipeline.support.AbstractFragmentActivityCallback;

public class OTPLoginActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        final OTPApplication application = (OTPApplication) getApplication();

        final TextView username = (TextView) findViewById(R.id.username);
        final TextView password = (TextView) findViewById(R.id.password);
        final Button login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                final ProgressDialog dialog = ProgressDialog.show(OTPLoginActivity.this, "Wait...", "Loging", true, true);

                application.login(OTPLoginActivity.this, user, pass, new AbstractFragmentActivityCallback<HeaderAndBody>() {
                    @Override
                    public void onSuccess(HeaderAndBody data) {
                        try {
                            application.retrieveOTPPath(OTPLoginActivity.this, new AbstractFragmentActivityCallback<List<OTPUser>>() {
                                @Override
                                public void onSuccess(List<OTPUser> data) {
                                    Intent intent = new Intent(OTPLoginActivity.this, OTPDisplay.class);
                                    intent.putExtra("otpauth", data.get(0).getUri());
                                    startActivity(intent);
                                    dialog.dismiss();
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    displayErrorMessage(getFragmentActivity(), e, dialog);
                                }
                            });
                        } catch (Exception e) {
                            displayErrorMessage(getFragmentActivity(), e, dialog);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        displayErrorMessage(getFragmentActivity(), e, dialog);
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void displayErrorMessage(Activity activity, Exception e, ProgressDialog dialog) {
        Log.e("Login", "An error occurrence", e);
        Toast.makeText(activity, "Login failed", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

}
