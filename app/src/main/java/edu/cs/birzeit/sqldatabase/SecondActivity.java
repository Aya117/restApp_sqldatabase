package edu.cs.birzeit.sqldatabase;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SecondActivity extends AppCompatActivity {

    private EditText edtBookTitle;
    private EditText edtBookCategory;
    private EditText edtBookPages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        setUpViews();

    }

    private void setUpViews() {
        edtBookTitle =  findViewById(R.id.edtBookTitle);
        edtBookCategory = findViewById(R.id.edtBookCategory);
        edtBookPages = findViewById(R.id.edtBookPages);
    }

    private String processRequest(String restUrl) throws UnsupportedEncodingException {
        String title = edtBookTitle.getText().toString();
        String category = edtBookCategory.getText().toString();
        String pages = edtBookPages.getText().toString();

        String data = URLEncoder.encode("title", "UTF-8")
                + "=" + URLEncoder.encode(title, "UTF-8");

        data += "&" + URLEncoder.encode("category", "UTF-8") + "="
                + URLEncoder.encode(category, "UTF-8");

        data += "&" + URLEncoder.encode("pages", "UTF-8")
                + "=" + URLEncoder.encode(pages, "UTF-8");

        String text = "";
        BufferedReader reader=null;

        // Send data
        try
        {

            // Defined URL  where to send data
            URL url = new URL(restUrl);

            // Send POST data request

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( data );
            wr.flush();

            // Get the server response

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = "";

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                // Append server response in string
                sb.append(line + "\n");
            }


            text = sb.toString();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {

                reader.close();
            }

            catch(Exception ex) {
                ex.printStackTrace();
            }
        }

        // Show response on activity
        return text;



    }

    private class SendPostRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return processRequest(urls[0]);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return "";
        }
        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(SecondActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }

    public void btnAddOnClick(View view) {
        String restUrl = "http://192.168.0.118/web/rest/addbook.php";
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    123);

        } else{
            SendPostRequest runner = new SendPostRequest();
            runner.execute(restUrl);
        }
    }
}