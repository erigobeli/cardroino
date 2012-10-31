package br.com.cardroino;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashScreenRunnable extends Activity implements Runnable {

	//3 segundos
	private final int DELAY = 2000;
	
	@Override
	public void onCreate(Bundle icicle){
		super.onCreate(icicle);
		
		//Esconde a barra de informacoes
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//Exibe o layout com imagem do splash screen
		setContentView(R.layout.activity_splash);
		
		Handler h = new Handler();
		h.postDelayed(this, DELAY);	
	}
	
	public void run(){
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}

}
