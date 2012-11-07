package br.com.cardroino;
//
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Esconde a barra de informacoes
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Trava orientacao
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        ShowMainWindow();
    }
    
    /*
     * Methods to call screens
     */
	   public void ShowMainWindow() {		   
	    	setContentView(R.layout.activity_main);
	    }
	   
	   public void ShowBluetoothSettings(){
	    	setContentView(R.layout.activity_bluetooth);
	   }
	   
    
    /*
     * Action button menu inflater 
     * * * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       MenuInflater inflater = getMenuInflater();
          inflater.inflate(R.menu.menu, menu);
          return true;
    }
    
    /*
     * Capture the option selected in menu 
     * * * */ 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       switch (item.getItemId()) {
          case R.id.menu_bluetooth:
        	  ShowBluetoothSettings();
             return true;
             
          case R.id.menu_settings:
        	  //ShowMenuSettings();
             return true;
             
          default:
             return super.onOptionsItemSelected(item);
       }
    }
}
