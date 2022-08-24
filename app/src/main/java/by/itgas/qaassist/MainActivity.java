package by.itgas.qaassist;

import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private static Socket clientSocket;
    private static ServerSocket server; // серверсокет
    private static BufferedWriter out; // поток записи в сокет

    static int good = 0;
    static int normal = 0;
    static int bad = 0;

    static boolean run = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    server = new ServerSocket(5000);
                    while (run){
                        clientSocket = server.accept();
                        try {
                            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                            long tsLong = System.currentTimeMillis()/1000;
                            String ts = Long.toString(tsLong);
                            out.write("HTTP/1.1 200 OK\r\n");
                            out.write("Content-Type: application/json\r\n\r\n");
                            out.write("\"data\":{\"good\":"+good+",\"norm\":"+normal+",\"bad\":"+bad+", \"time\":"+ts+"}");
                            out.flush();
                            good = 0;
                            normal = 0;
                            bad = 0;
                        } finally {
                            clientSocket.close();
                            out.close();
                        }
                    }
                } catch (Exception e) {
                    System.err.println(e.toString());
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        run = false;
        super.onDestroy();
    }

    public void onGood(View v){
        good++;
        thx();
    }
    public void onNormal(View v){
        normal++;
        thx();
    }
    public void onBad(View v){
        bad++;
        thx();
    }
    private void thx(){
        Toast t = Toast.makeText(this, "Спасибо за ваш отзыв", Toast.LENGTH_LONG);
        t.show();
    }
}
