package br.com.cardroino;
//



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
	
public class MainActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 1;
	
	Button btnProcuraBluet;
	
	ListView listDevicesFound;
	ArrayAdapter<String> btArrayAdapter;
	
	// Bluetooth
	BluetoothAdapter modulo_bluetooth;
	private BluetoothSocket socket;
    private InputStream input;
    private OutputStream output;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        
        // Instancia os componentes do bluetooth
        modulo_bluetooth = BluetoothAdapter.getDefaultAdapter();
        
        registerReceiver(ActionFoundReceiver, 
				new IntentFilter(BluetoothDevice.ACTION_FOUND));
        
        //Esconde a barra de informacoes
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Trava orientacao
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        ShowMainWindow();
    }
    
    /*
     * Methods to call screens
     */
    public void ShowMainWindow() 
    {		   
    	setContentView(R.layout.activity_main);
    	
    	VerificaBluetooth();
    }
	       
    
    public void ShowBluetoothSettings()
    {
    	setContentView(R.layout.activity_bluetooth);
    	
    	listDevicesFound = (ListView)findViewById(R.id.devicesfound);
    	
        btArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listDevicesFound.setAdapter(btArrayAdapter);
        
    	btnProcuraBluet = (Button)findViewById(R.id.btn_procura_bluet);
    	btnProcuraBluet.setOnClickListener(btnProcuraBluetOnClickListener);
    	
    	// Desconecta do bluetooth
    	desconectaBluetooth();
    	
    	// Selecionar os modulos Bluetooth
    	listDevicesFound.setOnItemClickListener(new OnItemClickListener()
    	{
    		
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    		{
    			String servername = parent.getItemAtPosition(position).toString();
    			conectaBluetooth(servername);
    			
    			setContentView(R.layout.activity_main);
    		}
    		
    	});
    	
    	
    	VerificaBluetooth();
    	
    	if(modulo_bluetooth.isEnabled())
		{
    		btnProcuraBluet.setEnabled(true);
		}
    }
    
    
    public void VerificaBluetooth()
    {
    	if(modulo_bluetooth == null)
    	{
    		Exibe("N„o possui bluetooth !", "Seu aparelho n„o possui suporte para bluetooth.");
        	finish();
    	}
    	else
    	{
    		if(modulo_bluetooth.isEnabled() == false)
    		{
    			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    		}
    	}
    }
    
    
    public Button.OnClickListener btnProcuraBluetOnClickListener = new Button.OnClickListener(){

    	//@Override
    	public void onClick(View arg0) 
    	{    		
    		modulo_bluetooth.cancelDiscovery();
    		
    		btArrayAdapter.clear();
    		modulo_bluetooth.startDiscovery();
    	}
    };
    
   
   
    
    
    @Override
    protected void onDestroy() 
    {
    	//TODO Auto-generated method stub
    	super.onDestroy();
    	unregisterReceiver(ActionFoundReceiver);
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	//TODO Auto-generated method stub
    	if(requestCode == REQUEST_ENABLE_BT)
    	{
    		VerificaBluetooth();
     	}
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
    
    
    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver()
    {
    	@Override
    	public void onReceive(Context context, Intent intent) 
    	{
    		//TODO Auto-generated method stub
    		String action = intent.getAction();
    		if(BluetoothDevice.ACTION_FOUND.equals(action)) 
    		{
    			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    			//btArrayAdapter.add(device.getName() + "\n" + device.getAddress());
    			btArrayAdapter.add(device.getAddress());
    			btArrayAdapter.notifyDataSetChanged();
    		}
    	}
    };
    	  
    
    /**
     * Conecta com bluetooth
     */
    public void conectaBluetooth(String servername)
    {
    	BluetoothDevice server = null;
		
    	// String servername = "00:12:08:09:01:65";	// Nome do Servidor
		
		modulo_bluetooth.startDiscovery();
		Set<BluetoothDevice> pairedDevices = modulo_bluetooth.getBondedDevices();
		
		
		if(pairedDevices.size() > 0) 
		{
			for (BluetoothDevice device : pairedDevices) 
			{ // La√ßo de Busca
				
	            if (servername.equals(device.getAddress())) 
	            { // Nomes Id√™nticos?
	                server = device; // Dispositivo Encontrado e Selecionado
	            }
	            
	        }
		}
		
		// Dispositivo Encontrado?
        if (server == null) 
        {
            // Mensagem de Erro ao Usu√°rio
            alert("Servidor n„o Pareado-" + servername);
            finish();
        }
		
		final BluetoothDevice computer = server;
		
		Thread connector = new Thread(){
            public void run() {
                // Identificador √önico do Servidor
                UUID ident = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                try {
                	
                    // Socket de Conex„o
                    BluetoothSocket s = computer.createRfcommSocketToServiceRecord(ident);
                        
                    // Conectar ao Servidor
                    s.connect();
                    
                    // Fluxos de Entrada e Saida de Dados
                    InputStream in = s.getInputStream();
                    OutputStream out = s.getOutputStream();
                    
                    // Captura de Objetos
                    socket = s; // Socket de Conex√£o
                    input  = in; // Fluxo de Entrada de Dados
                    output = out; // Fluxo de Sa√≠da de Dados
                    
                    // Informar sobre Conex„o
                    alert("Conex„o Aberta");
                    
                } catch (IOException e) { 
                    // Mensagem de Erro ao usuario
                    alert("Erro de Conex„o");
                    finish();
                }
            }
        };
        
        connector.start();
    }
    
    
    /**
     * Desconecta o bluetooth
     */
    public void desconectaBluetooth()
    {
	    super.onDestroy(); 
	    if (socket != null) 
	    { 
	        try 
	        {
	            socket.close();
	            alert("Desconectado");
	        } 
	        catch (IOException e) 
	        { 
	            alert("Erro ao Fechar Socket");
	        }
	    }
	    
    }
    
    
    /**
     * Manipulador de Mensagens
     */
    private final Handler h = new Handler() {
        public void handleMessage(Message msg) { // Manipular Mensagem
            String content = (String) msg.obj; // Captura do Conte√∫do da Mensagem
            Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
        }
    };
    
    
    /**
     * Envia Mensagens entre Fluxos de Execu√ß√£o
     * @param message Mensagem para Envio
     */
    public void alert(String message) {
        Message m = h.obtainMessage();
        m.obj = message;
        h.sendMessage(m);
    }
    
    	  
    public void Exibe(String titulo, String msg)
    {
    	AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
    	alerta.setMessage(msg);
    	alerta.setTitle(titulo);
    	alerta.setNeutralButton("OK", null);
    	alerta.show();
    }
    
    
}
