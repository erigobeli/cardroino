package br.com.cardroino;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
	
public class MainActivity extends Activity implements SensorEventListener {

	// Botões das direções
	ImageButton img_1;
	ImageButton img_2;
	ImageButton img_3;
	ImageButton img_4;
	ImageButton img_5;
	
	// Variaveis de controle do automodelo
	int direc_bluetooh = 0;
    int acele_bluetooth = 0;
    String tipo_controle; 
	int farol = 0;
    
    // Acelerometro
    private SensorManager mSensorManager;
    private Sensor mSensor;
	
	// Bluetooth
	BluetoothAdapter modulo_bluetooth;
	private BluetoothSocket socket;
    private InputStream input;
    private OutputStream output;
    Button btnProcuraBluet;
	ListView listDevicesFound;
	ArrayAdapter<String> btArrayAdapter;
	private static final int REQUEST_ENABLE_BT = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        // Instancia os componentes do bluetooth
        modulo_bluetooth = BluetoothAdapter.getDefaultAdapter();
        registerReceiver(ActionFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        
        //Esconde a barra de informacoes
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Trava orientacao
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        // Chama o metodo para o controle manual 
        ShowMainWindow();
    }
    
    
    // Chama a tela do controle manual
    public void ShowMainWindow() 
    {		
    	// Chama o layout activity_main
    	setContentView(R.layout.activity_main);
    
    	// Instancia os componentes do acelerometro
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
    	// Instanciando os componentes
        img_1 = (ImageButton) findViewById(R.id.img_1);
        img_2 = (ImageButton) findViewById(R.id.img_2);
        img_3 = (ImageButton) findViewById(R.id.img_3);
        img_4 = (ImageButton) findViewById(R.id.img_4);
        img_5 = (ImageButton) findViewById(R.id.img_5);
        
        // Botão para direita
        img_1.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) 
            {
            	if(tipo_controle.equals("2") && socket != null)
				{
	            	// Aciona
	                if(event.getAction() == MotionEvent.ACTION_DOWN) 
	                {
	                	enviarBluetooth("w");
	                	img_1.setImageResource(R.drawable.botao_dir_click);
	                }
	                
	                // Desliga
	                if (event.getAction() == MotionEvent.ACTION_UP) 
	                {
	                	enviarBluetooth("t");
	                	img_1.setImageResource(R.drawable.botao_dir);
	                }
				}
				return false;
            }
        });
        
        // Botão para esquerda
        img_2.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) 
			{
				if(tipo_controle.equals("2") && socket != null)
				{
					// Aciona
	            	if(event.getAction() == MotionEvent.ACTION_DOWN)
	                {
	                	enviarBluetooth("q");
	                	img_2.setImageResource(R.drawable.botao_esq_click);
	                }
	                // Desliga
	                if(event.getAction() == MotionEvent.ACTION_UP) 
	                {
	                	enviarBluetooth("t");
	                	img_2.setImageResource(R.drawable.botao_esq);
	                }
				}
				return false;
			}
		});
        
        // Botão para acelerar
        img_3.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) 
            {
            	if(tipo_controle.equals("2") && socket != null)
				{
	            	// Aciona
	                if(event.getAction() == MotionEvent.ACTION_DOWN) 
	                {
	                	enviarBluetooth("e");
	                	img_3.setImageResource(R.drawable.botao_cima_click);
	                }
	                // Desliga
	                if (event.getAction() == MotionEvent.ACTION_UP) 
	                {
	                	enviarBluetooth("y");
	                	img_3.setImageResource(R.drawable.botao_cima);
	                }
				}
				return false;
            }
        });

        // Botão para ré
        img_4.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) 
            {
            	if(tipo_controle.equals("2") && socket != null)
				{
	            	// Aciona
	                if(event.getAction() == MotionEvent.ACTION_DOWN) 
	                {
	                	enviarBluetooth("r");
	                	img_4.setImageResource(R.drawable.botao_baixo_click);
	                }
	                // Desliga
	                if (event.getAction() == MotionEvent.ACTION_UP) 
	                {
	                	enviarBluetooth("y");
	                	img_4.setImageResource(R.drawable.botao_baixo);
	                }
				}
				return false;
            }
        });
        
        // Acende e apaga farol
        img_5.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) 
            {
            	if(socket != null)
				{
	            	// Acende / Apaga
	                if(event.getAction() == MotionEvent.ACTION_DOWN) 
	                {
	                	if(farol == 0)
	                	{
	                		enviarBluetooth("i");
	                		farol = 1;
	                		img_5.setImageResource(R.drawable.farol_on);
	                		return false;
	                	}
	                	if(farol == 1)
	                	{
	                		enviarBluetooth("o");
	                		farol = 0;
	                		img_5.setImageResource(R.drawable.farol_off);
	                		return false;
	                	}
	                }
				}
				return false;
            }
        });        
    }
    
    // Chama a tela do acelerometro
    public void ShowMainAcelWindow() 
    {
    	// Chama o layout activity_main_acel
    	setContentView(R.layout.activity_main_acel);
        
    	// Instancia os componentes do acelerometro
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); 
        
        // Instanciando os componentes
        img_5 = (ImageButton) findViewById(R.id.img_5);
        
        // Acende e apaga farol
        img_5.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) 
            {
            	if(socket != null)
				{
	            	// Acende / Apaga
	                if(event.getAction() == MotionEvent.ACTION_DOWN) 
	                {
	                	if(farol == 0)
	                	{
	                		enviarBluetooth("i");
	                		farol = 1;
	                		img_5.setImageResource(R.drawable.farol_on);
	                		return false;
	                	}
	                	if(farol == 1)
	                	{
	                		enviarBluetooth("o");
	                		farol = 0;
	                		img_5.setImageResource(R.drawable.farol_off);
	                		return false;
	                	}
	                	
	                }
	                
				}
				return false;
            }
        });
    }    
    
    // Chama a tela de busca do bluetooth
    public void ShowBluetoothSettings()
    {
    	// Chama o layout activity_bluetooth
    	setContentView(R.layout.activity_bluetooth);
    	
    	// Instanciando os componentes
    	listDevicesFound = (ListView)findViewById(R.id.devicesfound);
    	btnProcuraBluet = (Button)findViewById(R.id.btn_procura_bluet);
    	btnProcuraBluet.setOnClickListener(btnProcuraBluetOnClickListener);
    	
    	// Cria a lista para exibir os modulos bluetooth
        btArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listDevicesFound.setAdapter(btArrayAdapter);
    	
    	// Desconecta do bluetooth
    	desconectaBluetooth();
    	
    	// Selecionar os modulos Bluetooth
    	listDevicesFound.setOnItemClickListener(new OnItemClickListener()
    	{
    		
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    		{
    			// Conecta ao bluetooth
    			String servername = parent.getItemAtPosition(position).toString();
    			conectaBluetooth(servername);
    			
    			ShowMainWindow();
    		}
    		
    	});
    	
    	// Chama o metodo para verificar o bluetooth
    	VerificaBluetooth();
    	
    	if(modulo_bluetooth.isEnabled())
		{
    		btnProcuraBluet.setEnabled(true);
		}
    }
    
    // Metodo que verifica se o bluetooh
    public void VerificaBluetooth()
    {
    	// Verifica se o aparelho possui bluetooth 
    	if(modulo_bluetooth == null)
    	{
    		Exibe("Não possui bluetooth !", "Seu aparelho não possui suporte para bluetooth.");
        	finish();
    	}
    	else
    	{
    		// Verifica se o bluetooth esta ativo, senão exibe uma mensagem para ativar
    		if(modulo_bluetooth.isEnabled() == false)
    		{
    			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    		}
    	}
    }
    
    // Botão que procura os modulos bluetooth
    public Button.OnClickListener btnProcuraBluetOnClickListener = new Button.OnClickListener(){
    	//@Override
    	public void onClick(View arg0) 
    	{    		
    		modulo_bluetooth.cancelDiscovery();
    		btArrayAdapter.clear();
    		modulo_bluetooth.startDiscovery();
    	}
    };
   
    // Sensor do acelerometro
    public void onSensorChanged(SensorEvent event) 
    {
    	if(tipo_controle.equals("1"))
		{
    		// Acelera
			if(event.values[0] <= 5 && event.values[2] > 0 && socket != null)
			{
				enviarBluetooth("e");
				acele_bluetooth = 1;
			}
			// Ré
			if(event.values[0] <= 5.5 && event.values[2] < 0 && socket != null)
			{
				enviarBluetooth("r");
				acele_bluetooth = 1;
			}
	    	// Direita: vel. 1
			if(event.values[1] >= 4.5 && event.values[1] < 7.5 && socket != null)
			{
				enviarBluetooth("w");
				direc_bluetooh = 1;
			}
			// Direita: vel. 2
			if(event.values[1] >= 7.5 && event.values[1] < 9 && socket != null)
			{
				enviarBluetooth("s");
				direc_bluetooh = 1;
			}
			// Direita: vel. 3
			if(event.values[1] >= 9 && socket != null)
			{
				enviarBluetooth("x");
				direc_bluetooh = 1;
			}			
			// Esquerda: vel. 1
			if(event.values[1] <= -4.5 && event.values[1] > -7.5 && socket != null)
			{
				enviarBluetooth("q");
				direc_bluetooh = 1;
			}
			// Esquerda: vel. 2
			if(event.values[1] <= -7.5 && event.values[1] > -9 && socket != null)
			{
				enviarBluetooth("a");
				direc_bluetooh = 1;
			}
			// Esquerda: vel. 3
			if(event.values[1] <= -9 && socket != null)
			{
				enviarBluetooth("z");
				direc_bluetooh = 1;
			}
			// Para de acelerar/re
			if((direc_bluetooh == 1 ) && (event.values[1] > -5 && event.values[1] < 5) && socket != null)
			{
				enviarBluetooth("t");
				direc_bluetooh = 0;
			}
			// Para a direção
			if((acele_bluetooth == 1 ) && (event.values[0] > 3.5 ) && socket != null)
			{
				enviarBluetooth("y");
				acele_bluetooth = 0;
			}
		}
    }
    
    // Metodo do acelerometro
    public void onAccuracyChanged(Sensor sensor, int accuracy) {    
    }
    
    @Override
    protected void onResume() 
    {
        super.onResume();
        // acelerometro
        mSensorManager.registerListener((SensorEventListener) this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onStop() 
    {
    	super.onStop();
    	// acelerometro
        mSensorManager.unregisterListener(this);
    }
    
    @Override
    protected void onDestroy() 
    {
    	//TODO Auto-generated method stub
    	super.onDestroy();
    	unregisterReceiver(ActionFoundReceiver);
    }
    
    @Override
    protected void onStart()
    {
    	super.onStart();
    	// Pega o tipo de controle que o usuario escolheu
    	SharedPreferences getData = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	tipo_controle = getData.getString("AcelManual", "1");
        
        // Exibe o tipo de controle correspondente 
        if(tipo_controle.equals("2"))
        {
        	ShowMainWindow();
        }
        else
        {
        	ShowMainAcelWindow();
        }
    }
    
    // Cria os menus
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    
    // Metodo para selecionar os menus
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
       switch (item.getItemId()) 
       {
          case R.id.menu_bluetooth:
        	  ShowBluetoothSettings();
    	  break;
             
          case R.id.menu_config:
        	  Intent i1 = new Intent("br.com.cardroino.CONFIGS");
        	  startActivity(i1);
    	  break;
    	  
          case R.id.aboutus:
          	Intent i2 = new Intent("br.com.cardroino.SOBRE");
          	startActivity(i2);
      	break;
    	  
          case R.id.sair:
        	  finish();
    	  break;
       }
       return super.onOptionsItemSelected(item);
    }
    
    // Metodo que busca os modulos bluetooths
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
    			btArrayAdapter.add(device.getAddress());
    			btArrayAdapter.notifyDataSetChanged();
    		}
    	}
    };
    
	// Envia comando pelo bluetooth
    public void enviarBluetooth(final String conteudo)
    {
        Thread sender = new Thread()
        {
            public void run() 
            {
                byte content[] = conteudo.getBytes();
                
                try 
                {
                    output.write(content.length);
                    output.write(content);
                } 
                catch (IOException e) 
                {
                    alert("Erro na Transferência");
                }
            }
        };
        sender.start();
    }
    
    // Conecta com bluetooth
    public void conectaBluetooth(String servername)
    {
    	BluetoothDevice server = null;
		modulo_bluetooth.startDiscovery();
		Set<BluetoothDevice> pairedDevices = modulo_bluetooth.getBondedDevices();
		
		// Verifica se o bluetooth esta pareado
		if(pairedDevices.size() > 0) 
		{
			for (BluetoothDevice device : pairedDevices) 
			{ 
				
	            if (servername.equals(device.getAddress())) 
	            {
	                server = device;
	            }
	        }
		}
		
		// Verifica se o modulo foi encontrado
        if (server == null) 
        {
            alert("Servidor não Pareado-" + servername);
            finish();
        }
		final BluetoothDevice computer = server;
		
		Thread connector = new Thread(){
            public void run() {
                // Identificador do modulo bluetooth
                UUID ident = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                try {
                	
                    // Socket de Conexão
                    BluetoothSocket s = computer.createRfcommSocketToServiceRecord(ident);
                        
                    // Conectar ao Servidor
                    s.connect();
                    
                    // Fluxos de entrada e saida de dados
                    InputStream in = s.getInputStream();
                    OutputStream out = s.getOutputStream();
                    
                    // Captura de objetos
                    socket = s; // Socket de conexão
                    input  = in; // Fluxo de entrada de dados
                    output = out; // Fluxo de saida de dados
                    
                    alert("Conectado");

                } catch (IOException e) { 
                    alert("Erro de Conexão");
                    finish();
                }
            }
        };
        connector.start();
    }
    
    // Desconecta bluetooth
    public void desconectaBluetooth()
    {
	    super.onDestroy(); 
	    if (socket != null) 
	    { 
	        try {
	            socket.close();
	            alert("Desconectado");
	        } 
	        catch (IOException e) { 
	            alert("Erro ao Fechar Socket");
	        }
	    }
	    
    }
    
    // Manipulador de mensagens (alerta)
    private final Handler h = new Handler() 
    {
        public void handleMessage(Message msg) 
        { 
            String content = (String) msg.obj; 
            Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
        }
    };
    
    // Metodo que exibe alerta
    public void alert(String message) 
    {
        Message m = h.obtainMessage();
        m.obj = message;
        h.sendMessage(m);
    }
    
    // Metodo que exibe mensagem
    public void Exibe(String titulo, String msg)
    {
    	AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
    	alerta.setMessage(msg);
    	alerta.setTitle(titulo);
    	alerta.setNeutralButton("OK", null);
    	alerta.show();
    }
    
}
