package br.com.cardroino;


import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class AboutUs extends Activity{

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        // Esconde a barra de informacoes
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        
        setContentView(R.layout.aboutus);
    }
	
}
