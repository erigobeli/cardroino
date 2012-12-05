package br.com.cardroino;

import android.os.Bundle;
import android.preference.PreferenceActivity;

// Classe para exibir a configuração 
public class Configs extends PreferenceActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.configs);
    }
	
}